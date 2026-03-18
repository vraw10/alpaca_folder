package com.groww.usstocks.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query parameters for latest bars, latest quotes, and snapshots.
 * Shared across multiple market data endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLatestMarketDataRequest {

    /**
     * Comma-separated list of symbols (e.g. "AAPL,MSFT").
     */
    private String symbols;

    /**
     * Data feed: iex, sip. Default varies by subscription.
     */
    private String feed;
}
