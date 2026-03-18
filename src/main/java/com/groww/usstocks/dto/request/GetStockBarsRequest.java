package com.groww.usstocks.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query parameters for retrieving historical stock bars.
 * GET /v2/stocks/bars
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStockBarsRequest {

    /**
     * Comma-separated list of symbols (e.g. "AAPL,MSFT").
     */
    private String symbols;

    /**
     * Bar timeframe (e.g. "1Min", "5Min", "15Min", "1Hour", "1Day", "1Week", "1Month").
     */
    private String timeframe;

    /**
     * Start date/time (RFC3339 or YYYY-MM-DD).
     */
    private String start;

    /**
     * End date/time (RFC3339 or YYYY-MM-DD).
     */
    private String end;

    /**
     * Maximum number of bars per symbol to return.
     */
    private Integer limit;

    /**
     * Pagination token from a previous response.
     */
    private String pageToken;

    /**
     * Sort order: asc or desc.
     */
    private String sort;

    /**
     * Adjustment: raw, split, dividend, all. Default: raw.
     */
    private String adjustment;

    /**
     * Data feed: iex, sip. Default varies by subscription.
     */
    private String feed;
}
