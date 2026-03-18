# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
US Stocks MVP — A Spring Boot REST API that proxies order placement, account management, and position queries to the Alpaca Broker API. Built for Groww's US equities trading feature.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Build**: Maven 3.6+ (no wrapper — use system `mvn`)
- **Libraries**: Lombok, Jackson, Spring Validation, Spring Actuator

## Environment Setup
Before running integration tests or the app, load credentials:
```bash
source .env
```
The `.env` file contains Alpaca API credentials and is gitignored.

## Build & Run Commands
```bash
mvn clean compile              # Compile
mvn test                       # Run tests (JUnit 5 via Surefire)
mvn spring-boot:run            # Run app (port 8081)
mvn clean package              # Build JAR
mvn clean package -DskipTests  # Build without tests
```

## Architecture
Layered architecture with a Strategy pattern for broker abstraction:

```
Controller → Service → BrokerService (interface) → AlpacaBrokerService
                ↓
          OrderValidator
```

- **Controller** (`controller/`): Thin REST layer, reads `X-Account-Id` header
- **Service** (`service/`): Business logic + validation, delegates to BrokerService
- **BrokerService** (`broker/BrokerService.java`): Vendor-agnostic interface — swap Alpaca for another broker without touching service/controller layers
- **Alpaca impl** (`broker/alpaca/`): OAuth2 client_credentials token management, RestTemplate HTTP calls, response mapping from Alpaca DTOs to internal DTOs
- **DTOs**: Separated into `dto/request/`, `dto/response/`, and `broker/alpaca/dto/` (Alpaca-internal)
- **Enums** (`model/enums/`): Internal enums with `@JsonValue`; Alpaca raw strings mapped via `AlpacaResponseMapper`
- **Exception handling**: `GlobalExceptionHandler` (@RestControllerAdvice) — logs internals, returns safe generic messages to clients

## Package Layout
```
com.groww.usstocks
├── broker/            # Broker abstraction + implementations
│   ├── BrokerService.java          # Vendor-agnostic interface
│   └── alpaca/                     # Alpaca implementation
│       ├── AlpacaApiConfig.java    # @ConfigurationProperties (alpaca.broker.*)
│       ├── AlpacaBrokerService.java
│       ├── AlpacaResponseMapper.java
│       ├── AlpacaRestClientConfig.java  # RestTemplate bean + OAuth2 interceptor
│       ├── AlpacaTokenService.java      # Token caching + refresh
│       └── dto/AlpacaOrderResponse.java # Raw Alpaca API response
├── controller/        # REST controllers (OrderController, AccountController)
├── dto/request/       # CreateOrderRequest, GetOrdersRequest, ReplaceOrderRequest
├── dto/response/      # OrderResponse, AccountResponse, PositionResponse, etc.
├── exception/         # BrokerApiException, OrderValidationException, GlobalExceptionHandler
├── model/enums/       # OrderStatus, OrderSide, OrderType, TimeInForce, OrderQueryStatus, SortDirection
├── service/           # OrderService, AccountService
└── validation/        # OrderValidator (business rules)
```

## API Endpoints
All endpoints require the `X-Account-Id` request header.

| Method | Path                         | Description            |
|--------|------------------------------|------------------------|
| POST   | /api/v1/orders               | Place order            |
| GET    | /api/v1/orders               | List orders (filtered) |
| GET    | /api/v1/orders/{orderId}     | Get order by ID        |
| GET    | /api/v1/orders/by-client-id  | Get by client_order_id |
| PATCH  | /api/v1/orders/{orderId}     | Replace/modify order   |
| DELETE | /api/v1/orders/{orderId}     | Cancel single order    |
| DELETE | /api/v1/orders               | Cancel all orders      |
| POST   | /api/v1/orders/estimate      | Estimate order         |
| GET    | /api/v1/account              | Account details        |
| GET    | /api/v1/account/positions    | Open positions         |
| GET    | /api/v1/account/trading-limits | Trading limits       |

## MVP Business Rules (OrderValidator)
- Only `DAY` time_in_force allowed
- `qty` and `notional` are mutually exclusive; at least one required
- `notional` only valid for MARKET orders
- `limit_price` required for LIMIT and STOP_LIMIT orders
- `stop_price` required for STOP and STOP_LIMIT orders
- MARKET orders must NOT have limit_price or stop_price
- `extended_hours` only valid with LIMIT + DAY
- `client_order_id` max 48 characters

## Testing
- **Framework**: JUnit 5 (via spring-boot-starter-test)
- **Run**: `mvn test`
- **Conventions**: `@Nested` inner classes for grouping, `@ParameterizedTest` with `@CsvSource`, `@DisplayName` for readability
- **Test location**: `src/test/java/` mirrors main source structure
- **Current coverage**: `AlpacaResponseMapperTest` only — unit test without Spring context

## Configuration
- **Port**: 8081
- **Config file**: `src/main/resources/application.yml`
- **Alpaca sandbox URLs** are configured; credentials should be set via environment variables (override `alpaca.broker.client-id` and `alpaca.broker.client-secret`)
- **Jackson**: `non_null` inclusion, ISO date strings, ignore unknown properties
- **Actuator**: Only `health` and `info` endpoints exposed
- **Logging**: DEBUG level for `com.groww.usstocks` and `org.springframework.web.client`

## Code Style
- **Google Java Format** enabled (IntelliJ plugin)
- **Lombok** used extensively: `@Data`, `@Builder`, `@Slf4j`, `@RequiredArgsConstructor`, `@NoArgsConstructor`, `@AllArgsConstructor`
- All DTOs use `@JsonProperty` for snake_case JSON field mapping
- All enums use `@JsonValue` for lowercase serialization
- Broker-specific DTOs are package-private to `broker/alpaca/dto/`; public DTOs in `dto/response/` are vendor-agnostic

## Key Design Decisions
1. **Broker abstraction**: `BrokerService` interface allows swapping Alpaca for DriveWealth, Interactive Brokers, etc. without changing service or controller code
2. **Separate DTO layers**: Alpaca raw DTOs (`AlpacaOrderResponse`) are mapped to internal DTOs (`OrderResponse`) via `AlpacaResponseMapper` — isolates vendor changes
3. **Stateless**: No database; pure proxy to Alpaca APIs with validation
4. **OAuth2 token caching**: `AlpacaTokenService` caches tokens and refreshes 60 seconds before expiry
5. **Safe error handling**: `GlobalExceptionHandler` never leaks internal details to clients; logs full errors server-side

# Project Context for Claude Code

## Alpaca Broker API – Create Order for Account

### Endpoint
```
POST /v1/trading/accounts/{account_id}/orders
```

### Base URLs
- **Sandbox:** `https://broker-api.sandbox.alpaca.markets`
- **Production:** `https://broker-api.alpaca.markets`

### Authentication
HTTP Basic Auth — use your Broker API key as username and secret as password.

```
Authorization: Basic base64(KEY:SECRET)
```

---

## Request Body Schema

### Required Fields
| Field | Type | Description |
|---|---|---|
| `type` | string | Order type (see allowed values below) |
| `time_in_force` | string | Duration the order remains active |

### Common Optional Fields
| Field | Type | Description |
|---|---|---|
| `symbol` | string | Asset symbol e.g. `"AAPL"`, `"ETH/USD"`, `"AAPL250620C00100000"` |
| `qty` | decimal string | Number of shares/contracts/coins. Cannot use with `notional`. |
| `notional` | decimal string | Dollar amount to trade. Cannot use with `qty`. |
| `side` | string | `"buy"` or `"sell"`. Required except for `mleg` orders. |
| `limit_price` | decimal string | Required if type is `limit` or `stop_limit` |
| `stop_price` | decimal string | Required if type is `stop` or `stop_limit` |
| `trail_price` | decimal string | Required (or `trail_percent`) if type is `trailing_stop` |
| `trail_percent` | decimal string | Required (or `trail_price`) if type is `trailing_stop` |
| `order_class` | string | `"simple"`, `"bracket"`, `"oco"`, `"oto"`, `"mleg"` |
| `extended_hours` | boolean | Pre/aftermarket eligibility. Only with `limit` + `time_in_force=day` |
| `client_order_id` | string | Your idempotency key (≤ 128 chars) |
| `commission` | decimal string | Flat fee to charge the end customer |
| `commission_type` | string | `"notional"` (default), `"qty"`, or `"bps"` |
| `position_intent` | string | `"buy_to_open"`, `"buy_to_close"`, `"sell_to_open"`, `"sell_to_close"` |

---

## Enum Reference

### `type` — Order Types
```
market | limit | stop | stop_limit | trailing_stop
```

### `time_in_force` — Time In Force
```
day | gtc | opg | cls | ioc | fok
```
- `day` — Valid for current trading day only
- `gtc` — Good Till Canceled
- `opg` — Market/limit on open (MOO/LOO)
- `cls` — Market/limit on close (MOC/LOC)
- `ioc` — Immediate Or Cancel
- `fok` — Fill Or Kill

### `side` — Order Side
```
buy | sell | buy_minus | sell_plus | sell_short | sell_short_exempt
```

### `order_class`
```
simple | bracket | oco | oto | mleg
```

### `position_intent`
```
buy_to_open | buy_to_close | sell_to_open | sell_to_close
```

### `commission_type`
```
notional | qty | bps
```

---

## Asset-Type Constraints

| Asset Class | Supported Order Types | Supported Time-in-Force |
|---|---|---|
| **Equity** (`us_equity`) | market, limit, stop, stop_limit, trailing_stop | day, gtc, opg, cls, ioc, fok |
| **Options** (`us_option`) | market, limit | day |
| **Crypto** (`crypto`) | market, limit, stop_limit | gtc, ioc |
| **Fixed Income** | market, limit | day only |

> **Notes:**
> - Options trading requires partner enablement (Options BETA)
> - Fixed Income requires partner enablement; order replacement not supported
> - Fractional orders supported for equities and crypto (use `qty` or `notional`)

---

## Request Examples

### Buy Equity (AAPL limit order)
```json
{
  "symbol": "AAPL",
  "qty": "2",
  "side": "buy",
  "type": "limit",
  "limit_price": "150",
  "time_in_force": "gtc"
}
```

### Buy Crypto (ETH)
```json
{
  "symbol": "ETH/USD",
  "qty": "0.02",
  "side": "buy",
  "type": "limit",
  "limit_price": "2100",
  "time_in_force": "gtc"
}
```

### Buy Option Contract
```json
{
  "symbol": "AAPL250620C00100000",
  "qty": "2",
  "side": "buy",
  "type": "limit",
  "limit_price": "10",
  "time_in_force": "day"
}
```

### Buy Fixed Income (US T-Bill)
```json
{
  "symbol": "US912797QN08",
  "qty": "5000",
  "side": "buy",
  "type": "limit",
  "limit_price": "99.15",
  "time_in_force": "day"
}
```

---

## Response (200 OK) — Order Object

Key fields returned:

| Field | Type | Description |
|---|---|---|
| `id` | UUID string | Alpaca-generated order ID |
| `client_order_id` | string | Your provided ID (or auto-generated) |
| `status` | string | Current order status (see below) |
| `symbol` | string | Asset symbol |
| `asset_class` | string | `us_equity`, `us_option`, or `crypto` |
| `qty` | decimal string | Ordered quantity |
| `filled_qty` | decimal string | How much has been filled |
| `filled_avg_price` | decimal string | Average fill price (null until filled) |
| `type` | string | Order type |
| `side` | string | Buy or sell |
| `time_in_force` | string | TIF value |
| `limit_price` | decimal string | Limit price (null if not applicable) |
| `stop_price` | decimal string | Stop price (null if not applicable) |
| `legs` | array | Sub-orders for bracket/mleg orders |
| `created_at` | ISO datetime | When order was created |
| `filled_at` | ISO datetime | When order was filled (null if not) |

### Order Statuses
```
new | pending_new | accepted | accepted_for_bidding |
partially_filled | filled | done_for_day |
canceled | pending_cancel | expired | replaced |
pending_replace | stopped | rejected | suspended | calculated
```

---

## Error Responses

| Code | Meaning |
|---|---|
| `400` | Malformed input — check field types and required fields |
| `403` | Forbidden — check API credentials and account permissions |
| `404` | Account not found — check `account_id` in URL |
| `422` | Invalid parameter combination — check asset-type constraints above |


# Alpaca Broker API — Get US Market Calendar

## Overview

**Endpoint:** `GET /v1/calendar`  
**Auth:** HTTP Basic Auth  
**Servers:**
- Sandbox: `https://broker-api.sandbox.alpaca.markets`
- Production: `https://broker-api.alpaca.markets`

### Authentication
HTTP Basic Auth — use your Broker API key as username and secret as password.

The calendar API serves the full list of US market days from **1970 to 2029**. It can be filtered by start/end date and supports both trading and settlement date types. Responses include market open/close times and account for early closures.

---

## Query Parameters

| Parameter   | Type     | Format      | Required | Description |
|-------------|----------|-------------|----------|-------------|
| `start`     | string   | date-time   | No       | First date to retrieve data for (inclusive). |
| `end`       | string   | date-time   | No       | Last date to retrieve data for (inclusive). |
| `date_type` | string   | enum        | No       | `TRADING` (default) or `SETTLEMENT`. Controls whether `start`/`end` match trading or settlement dates. |

---

## Response Schema — `legacy_calendar_day`

Each item in the response array is a calendar day object:

| Field             | Type   | Format | Required | Example        | Description |
|------------------|--------|--------|----------|----------------|-------------|
| `date`            | string | date   | Yes      | `2025-01-02`   | Trading date in `YYYY-MM-DD` format. |
| `open`            | string | —      | Yes      | `09:30`        | Market open time in `HH:MM` format. |
| `close`           | string | —      | Yes      | `16:00`        | Market close time in `HH:MM` format. |
| `session_open`    | string | —      | Yes      | `0400`         | Extended session open time in `HHMM` format. |
| `session_close`   | string | —      | Yes      | `2000`         | Extended session close time in `HHMM` format. |
| `settlement_date` | string | date   | Yes      | `2025-01-03`   | Settlement date for the trade date, in `YYYY-MM-DD` format. |

The full response is an **array** of these objects.

---

## Example Response

```json
[
  {
    "date": "2025-01-02",
    "open": "09:30",
    "close": "16:00",
    "session_open": "0400",
    "session_close": "2000",
    "settlement_date": "2025-01-03"
  }
]
```

---

## Error Responses

| Status | Description |
|--------|-------------|
| `200`  | OK — returns array of calendar day objects. |
| `400`  | Bad request — one or more parameters are invalid. |
| `429`  | Too many requests — rate limit exceeded. |
| `500`  | Internal server error — retry later or contact support. |

---

## Rate Limiting

Rate limit info is returned in response headers:

| Header                  | Type    | Example        | Description |
|-------------------------|---------|----------------|-------------|
| `X-RateLimit-Limit`     | integer | `100`          | Request limit per minute. |
| `X-RateLimit-Remaining` | integer | `90`           | Remaining requests this minute. |
| `X-RateLimit-Reset`     | integer | `1674044551`   | UNIX epoch timestamp when quota resets. |

---

## Notes

- `date_type=TRADING` (default): filters by the trading date.
- `date_type=SETTLEMENT`: filters by the settlement date instead.
- Times in `open`/`close` use `HH:MM` format; `session_open`/`session_close` use `HHMM` (no colon).
- All fields in `legacy_calendar_day` are **required** in the response.

