const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');

// Load credentials from .env
const envPath = path.join(__dirname, '..', '.env');
const envContent = fs.readFileSync(envPath, 'utf8');
const env = {};
envContent.split('\n').forEach(line => {
  const match = line.match(/^export\s+(\w+)=(.+)/);
  if (match) env[match[1]] = match[2].trim();
});

const CLIENT_ID = env.ALPACA_BROKER_CLIENT_ID;
const CLIENT_SECRET = env.ALPACA_BROKER_CLIENT_SECRET;
const AUTH = 'Basic ' + Buffer.from(CLIENT_ID + ':' + CLIENT_SECRET).toString('base64');
const ALPACA_HOST = 'broker-api.sandbox.alpaca.markets';

const STATIC_DIR = path.join(__dirname, '..', 'dashboard-app', 'src', 'main', 'resources', 'static');

function alpacaRequest(apiPath, res) {
  const options = {
    hostname: ALPACA_HOST,
    path: apiPath,
    headers: { 'Authorization': AUTH, 'Accept': 'application/json' }
  };
  https.get(options, (alpacaRes) => {
    let body = '';
    alpacaRes.on('data', chunk => body += chunk);
    alpacaRes.on('end', () => {
      res.writeHead(alpacaRes.statusCode, { 'Content-Type': 'application/json' });
      res.end(body);
    });
  }).on('error', (err) => {
    res.writeHead(502, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: err.message }));
  });
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');

  if (url.pathname === '/api/account') {
    const accountId = url.searchParams.get('accountId');
    alpacaRequest(`/v1/trading/accounts/${accountId}/account`, res);
  } else if (url.pathname === '/api/orders') {
    const accountId = url.searchParams.get('accountId');
    alpacaRequest(`/v1/trading/accounts/${accountId}/orders?status=open`, res);
  } else {
    // Serve static files (index.html)
    let filePath = url.pathname === '/' ? '/index.html' : url.pathname;
    const fullPath = path.join(STATIC_DIR, filePath);
    fs.readFile(fullPath, (err, data) => {
      if (err) {
        res.writeHead(404);
        res.end('Not found');
      } else {
        const ext = path.extname(fullPath);
        const types = { '.html': 'text/html', '.css': 'text/css', '.js': 'application/javascript' };
        res.writeHead(200, { 'Content-Type': types[ext] || 'text/plain' });
        res.end(data);
      }
    });
  }
});

server.listen(3000, () => {
  console.log('Proxy server running at http://localhost:3000');
  console.log('Serving dashboard + proxying Alpaca API calls');
});
