const http = require("http");
const https = require("https");
const fs = require("fs");
const path = require("path");

const PORT = 3003;
const ALPACA_DATA_URL = "https://data.sandbox.alpaca.markets";
const ALPACA_TOKEN_URL =
  "https://authx.sandbox.alpaca.markets/v1/oauth2/token";
const ALPACA_KEY =
  process.env.ALPACA_BROKER_CLIENT_ID || process.env.APCA_API_KEY_ID || "";
const ALPACA_SECRET =
  process.env.ALPACA_BROKER_CLIENT_SECRET ||
  process.env.APCA_API_SECRET_KEY ||
  "";

// --- OAuth2 token cache ---
let cachedToken = null;
let tokenExpiresAt = 0;

function fetchToken() {
  return new Promise((resolve, reject) => {
    const postData = `grant_type=client_credentials&client_id=${encodeURIComponent(ALPACA_KEY)}&client_secret=${encodeURIComponent(ALPACA_SECRET)}`;
    const urlObj = new URL(ALPACA_TOKEN_URL);
    const options = {
      hostname: urlObj.hostname,
      path: urlObj.pathname,
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Content-Length": Buffer.byteLength(postData),
      },
    };

    const req = https.request(options, (res) => {
      let body = "";
      res.on("data", (chunk) => (body += chunk));
      res.on("end", () => {
        if (res.statusCode !== 200) {
          return reject(new Error(`Token request failed: ${res.statusCode} ${body}`));
        }
        try {
          const data = JSON.parse(body);
          cachedToken = data.access_token;
          // Refresh 60s before expiry
          tokenExpiresAt = Date.now() + (data.expires_in - 60) * 1000;
          resolve(cachedToken);
        } catch (e) {
          reject(new Error(`Failed to parse token response: ${e.message}`));
        }
      });
    });
    req.on("error", reject);
    req.write(postData);
    req.end();
  });
}

async function getToken() {
  if (cachedToken && Date.now() < tokenExpiresAt) {
    return cachedToken;
  }
  return fetchToken();
}

// --- Alpaca data proxy ---
async function proxyAlpaca(alpacaPath, res) {
  let token;
  try {
    token = await getToken();
  } catch (err) {
    res.writeHead(502, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ error: `Token error: ${err.message}` }));
    return;
  }

  const url = `${ALPACA_DATA_URL}${alpacaPath}`;
  const options = {
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  };

  https
    .get(url, options, (upstream) => {
      let body = "";
      upstream.on("data", (chunk) => (body += chunk));
      upstream.on("end", () => {
        res.writeHead(upstream.statusCode, {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        });
        res.end(body);
      });
    })
    .on("error", (err) => {
      res.writeHead(502, { "Content-Type": "application/json" });
      res.end(JSON.stringify({ error: err.message }));
    });
}

function serveStatic(filePath, contentType, res) {
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404);
      res.end("Not found");
      return;
    }
    res.writeHead(200, { "Content-Type": contentType });
    res.end(data);
  });
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);

  // Proxy: /api/snapshots?symbols=AAPL,MSFT,...
  if (url.pathname === "/api/snapshots") {
    const symbols = url.searchParams.get("symbols") || "";
    proxyAlpaca(`/v2/stocks/snapshots?symbols=${symbols}`, res);
    return;
  }

  // Serve index.html
  if (url.pathname === "/" || url.pathname === "/index.html") {
    serveStatic(path.join(__dirname, "index.html"), "text/html", res);
    return;
  }

  res.writeHead(404);
  res.end("Not found");
});

server.listen(PORT, () => {
  console.log(`NASDAQ 100 Heatmap running at http://localhost:${PORT}`);
});
