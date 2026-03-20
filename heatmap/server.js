const http = require("http");
const https = require("https");
const fs = require("fs");
const path = require("path");

const PORT = 3001;
const ALPACA_DATA_URL = "https://data.sandbox.alpaca.markets";
const ALPACA_KEY =
  process.env.ALPACA_BROKER_CLIENT_ID || process.env.APCA_API_KEY_ID || "";
const ALPACA_SECRET =
  process.env.ALPACA_BROKER_CLIENT_SECRET ||
  process.env.APCA_API_SECRET_KEY ||
  "";

function proxyAlpaca(alpacaPath, res) {
  const url = `${ALPACA_DATA_URL}${alpacaPath}`;
  const basicAuth = Buffer.from(`${ALPACA_KEY}:${ALPACA_SECRET}`).toString(
    "base64"
  );
  const options = {
    headers: {
      Authorization: `Basic ${basicAuth}`,
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
