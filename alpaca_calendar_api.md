# Alpaca Broker API — Get US Market Calendar

## Overview

**Endpoint:** `GET /v1/calendar`  
**Auth:** HTTP Basic Auth  
**Servers:**
- Sandbox: `https://broker-api.sandbox.alpaca.markets`
- Production: `https://broker-api.alpaca.markets`

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
