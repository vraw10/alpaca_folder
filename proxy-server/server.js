const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');
const querystring = require('querystring');

// ---------------------------------------------------------------------------
// Config
// ---------------------------------------------------------------------------
const envPath = path.join(__dirname, '..', '.env');
if (fs.existsSync(envPath)) {
  fs.readFileSync(envPath, 'utf8').split('\n').forEach(line => {
    const match = line.match(/^export\s+(\w+)=(.+)/);
    if (match) process.env[match[1]] = match[2].trim();
  });
}

const CLIENT_ID = process.env.ALPACA_BROKER_CLIENT_ID;
const CLIENT_SECRET = process.env.ALPACA_BROKER_CLIENT_SECRET;
const TOKEN_URL_HOST = 'authx.sandbox.alpaca.markets';
const TOKEN_URL_PATH = '/v1/oauth2/token';
const ALPACA_HOST = 'broker-api.sandbox.alpaca.markets';
const PORT = 3000;
const TOKEN_REFRESH_BUFFER_SECONDS = 60;

// ---------------------------------------------------------------------------
// OAuth2 Token Service
// ---------------------------------------------------------------------------
let cachedToken = null;
let tokenExpiresAt = 0; // epoch ms

function getAccessToken() {
  return new Promise((resolve, reject) => {
    const now = Date.now();
    if (cachedToken && now < tokenExpiresAt) {
      return resolve(cachedToken);
    }
    refreshToken().then(resolve).catch(reject);
  });
}

function refreshToken() {
  return new Promise((resolve, reject) => {
    const body = querystring.stringify({
      grant_type: 'client_credentials',
      client_id: CLIENT_ID,
      client_secret: CLIENT_SECRET,
    });

    const options = {
      hostname: TOKEN_URL_HOST,
      path: TOKEN_URL_PATH,
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Content-Length': Buffer.byteLength(body),
      },
    };

    const req = https.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        if (res.statusCode !== 200) {
          return reject(new Error(`Token request failed: ${res.statusCode} ${data}`));
        }
        try {
          const parsed = JSON.parse(data);
          cachedToken = parsed.access_token;
          const expiresIn = parsed.expires_in || 3600;
          tokenExpiresAt = Date.now() + (expiresIn - TOKEN_REFRESH_BUFFER_SECONDS) * 1000;
          console.log(`[Token] Acquired, expires in ${expiresIn}s`);
          resolve(cachedToken);
        } catch (e) {
          reject(new Error(`Token parse error: ${e.message}`));
        }
      });
    });

    req.on('error', (err) => reject(new Error(`Token request error: ${err.message}`)));
    req.write(body);
    req.end();
  });
}

// ---------------------------------------------------------------------------
// Alpaca Proxy Helper
// ---------------------------------------------------------------------------
function alpacaRequest(method, apiPath, body, res) {
  getAccessToken().then(token => {
    const options = {
      hostname: ALPACA_HOST,
      path: apiPath,
      method: method,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    };

    if (body) {
      const bodyStr = typeof body === 'string' ? body : JSON.stringify(body);
      options.headers['Content-Length'] = Buffer.byteLength(bodyStr);
    }

    const proxyReq = https.request(options, (alpacaRes) => {
      let data = '';
      alpacaRes.on('data', chunk => data += chunk);
      alpacaRes.on('end', () => {
        const headers = { 'Content-Type': 'application/json' };
        // Pass through CORS headers for browser usage
        headers['Access-Control-Allow-Origin'] = '*';
        headers['Access-Control-Allow-Methods'] = 'GET, POST, PATCH, DELETE, OPTIONS';
        headers['Access-Control-Allow-Headers'] = 'Content-Type, X-Account-Id';
        res.writeHead(alpacaRes.statusCode, headers);
        res.end(data);
      });
    });

    proxyReq.on('error', (err) => {
      res.writeHead(502, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'Upstream error', message: err.message }));
    });

    if (body) {
      proxyReq.write(typeof body === 'string' ? body : JSON.stringify(body));
    }
    proxyReq.end();
  }).catch(err => {
    res.writeHead(500, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Token error', message: err.message }));
  });
}

// ---------------------------------------------------------------------------
// Request body reader
// ---------------------------------------------------------------------------
function readBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', chunk => data += chunk);
    req.on('end', () => resolve(data));
  });
}

// ---------------------------------------------------------------------------
// Router
// ---------------------------------------------------------------------------
const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);
  const pathname = url.pathname;
  const method = req.method.toUpperCase();
  const accountId = req.headers['x-account-id'];

  // CORS preflight
  if (method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PATCH, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, X-Account-Id',
    });
    return res.end();
  }

  // --- Order endpoints ---

  // POST /api/v1/orders — Create order
  if (pathname === '/api/v1/orders' && method === 'POST') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    const body = await readBody(req);
    return alpacaRequest('POST', `/v1/trading/accounts/${accountId}/orders`, body, res);
  }

  // GET /api/v1/orders — List orders
  if (pathname === '/api/v1/orders' && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    const qs = buildQueryString(url, ['status', 'limit', 'after', 'until', 'direction', 'nested', 'symbols', 'side']);
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/orders${qs}`, null, res);
  }

  // DELETE /api/v1/orders — Cancel all orders
  if (pathname === '/api/v1/orders' && method === 'DELETE') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('DELETE', `/v1/trading/accounts/${accountId}/orders`, null, res);
  }

  // GET /api/v1/orders/by-client-id — Get order by client order ID
  if (pathname === '/api/v1/orders/by-client-id' && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    const clientOrderId = url.searchParams.get('clientOrderId');
    if (!clientOrderId) return sendError(res, 400, 'clientOrderId query parameter is required');
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/orders:by_client_order_id?client_order_id=${encodeURIComponent(clientOrderId)}`, null, res);
  }

  // POST /api/v1/orders/estimate — Estimate order
  if (pathname === '/api/v1/orders/estimate' && method === 'POST') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    const body = await readBody(req);
    return alpacaRequest('POST', `/v1/trading/accounts/${accountId}/orders/estimation`, body, res);
  }

  // GET /api/v1/orders/:orderId — Get order by ID
  const orderByIdMatch = pathname.match(/^\/api\/v1\/orders\/([^/]+)$/);
  if (orderByIdMatch && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/orders/${orderByIdMatch[1]}`, null, res);
  }

  // PATCH /api/v1/orders/:orderId — Replace order
  if (orderByIdMatch && method === 'PATCH') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    const body = await readBody(req);
    return alpacaRequest('PATCH', `/v1/trading/accounts/${accountId}/orders/${orderByIdMatch[1]}`, body, res);
  }

  // DELETE /api/v1/orders/:orderId — Cancel order
  if (orderByIdMatch && method === 'DELETE') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('DELETE', `/v1/trading/accounts/${accountId}/orders/${orderByIdMatch[1]}`, null, res);
  }

  // --- Account endpoints ---

  // GET /api/v1/account/trading-limits
  if (pathname === '/api/v1/account/trading-limits' && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/limits`, null, res);
  }

  // GET /api/v1/account/trading — Trading account details (buying_power, cash, etc.)
  if (pathname === '/api/v1/account/trading' && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/account`, null, res);
  }

  // GET /api/v1/account
  if (pathname === '/api/v1/account' && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('GET', `/v1/accounts/${accountId}`, null, res);
  }

  // --- Position endpoints ---

  // GET /api/v1/positions
  if (pathname === '/api/v1/positions' && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/positions`, null, res);
  }

  // GET /api/v1/positions/:symbolOrAssetId
  const positionMatch = pathname.match(/^\/api\/v1\/positions\/([^/]+)$/);
  if (positionMatch && method === 'GET') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('GET', `/v1/trading/accounts/${accountId}/positions/${positionMatch[1]}`, null, res);
  }

  // DELETE /api/v1/positions/:symbolOrAssetId — Close position
  if (positionMatch && method === 'DELETE') {
    if (!accountId) return sendError(res, 400, 'X-Account-Id header is required');
    return alpacaRequest('DELETE', `/v1/trading/accounts/${accountId}/positions/${positionMatch[1]}`, null, res);
  }

  // --- Asset endpoints (no X-Account-Id required) ---

  // GET /api/v1/assets
  if (pathname === '/api/v1/assets' && method === 'GET') {
    const qs = buildQueryString(url, ['status', 'asset_class', 'exchange']);
    return alpacaRequest('GET', `/v1/assets${qs}`, null, res);
  }

  // GET /api/v1/assets/:symbolOrAssetId
  const assetMatch = pathname.match(/^\/api\/v1\/assets\/([^/]+)$/);
  if (assetMatch && method === 'GET') {
    return alpacaRequest('GET', `/v1/assets/${assetMatch[1]}`, null, res);
  }

  // --- Market calendar (no X-Account-Id required) ---

  // GET /api/v1/market-data/calendar
  if (pathname === '/api/v1/market-data/calendar' && method === 'GET') {
    const qs = buildQueryString(url, ['start', 'end', 'date_type']);
    return alpacaRequest('GET', `/v1/calendar${qs}`, null, res);
  }

  // --- Health check ---
  if (pathname === '/health' && method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({ status: 'up' }));
  }

  // --- Static files (UI) ---
  serveStatic(pathname, res);
});

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------
function sendError(res, status, message) {
  res.writeHead(status, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ error: message }));
}

const STATIC_DIR = path.join(__dirname, 'public');
const MIME_TYPES = { '.html': 'text/html', '.css': 'text/css', '.js': 'application/javascript', '.json': 'application/json', '.png': 'image/png', '.svg': 'image/svg+xml' };

function serveStatic(pathname, res) {
  let filePath = pathname === '/' ? '/index.html' : pathname;
  const fullPath = path.join(STATIC_DIR, filePath);
  // Prevent directory traversal
  if (!fullPath.startsWith(STATIC_DIR)) {
    res.writeHead(403);
    return res.end('Forbidden');
  }
  fs.readFile(fullPath, (err, data) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      return res.end(JSON.stringify({ error: 'Not found', path: pathname }));
    }
    const ext = path.extname(fullPath);
    res.writeHead(200, { 'Content-Type': MIME_TYPES[ext] || 'text/plain' });
    res.end(data);
  });
}

function buildQueryString(url, allowedParams) {
  const params = [];
  for (const key of allowedParams) {
    const val = url.searchParams.get(key);
    if (val != null) params.push(`${key}=${encodeURIComponent(val)}`);
  }
  return params.length ? '?' + params.join('&') : '';
}

// ---------------------------------------------------------------------------
// Start
// ---------------------------------------------------------------------------
server.listen(PORT, () => {
  console.log(`Proxy server running at http://localhost:${PORT}`);
  console.log('Routes:');
  console.log('  POST   /api/v1/orders              — Create order');
  console.log('  GET    /api/v1/orders              — List orders');
  console.log('  GET    /api/v1/orders/:id          — Get order by ID');
  console.log('  GET    /api/v1/orders/by-client-id — Get by client_order_id');
  console.log('  PATCH  /api/v1/orders/:id          — Replace order');
  console.log('  DELETE /api/v1/orders/:id          — Cancel order');
  console.log('  DELETE /api/v1/orders              — Cancel all orders');
  console.log('  POST   /api/v1/orders/estimate     — Estimate order');
  console.log('  GET    /api/v1/account             — Account details');
  console.log('  GET    /api/v1/account/trading     — Trading account (buying power, cash)');
  console.log('  GET    /api/v1/account/trading-limits — Trading limits');
  console.log('  GET    /api/v1/positions            — List positions');
  console.log('  GET    /api/v1/positions/:symbol    — Get position');
  console.log('  DELETE /api/v1/positions/:symbol    — Close position');
  console.log('  GET    /api/v1/assets               — List assets');
  console.log('  GET    /api/v1/assets/:symbol       — Get asset');
  console.log('  GET    /api/v1/market-data/calendar — Market calendar');
  console.log('  GET    /health                      — Health check');
});
