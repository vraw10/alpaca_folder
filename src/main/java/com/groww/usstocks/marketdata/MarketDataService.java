package com.groww.usstocks.marketdata;

import com.groww.usstocks.dto.request.GetLatestMarketDataRequest;
import com.groww.usstocks.dto.request.GetStockBarsRequest;
import com.groww.usstocks.dto.response.LatestBarsResponse;
import com.groww.usstocks.dto.response.LatestQuotesResponse;
import com.groww.usstocks.dto.response.SnapshotsResponse;
import com.groww.usstocks.dto.response.StockBarsResponse;

/**
 * Vendor-agnostic market data interface.
 * <p>
 * Similar to {@link com.groww.usstocks.broker.BrokerService} but for
 * market data operations. Separate concern — different base URL and auth.
 */
public interface MarketDataService {

    StockBarsResponse getHistoricalBars(GetStockBarsRequest request);

    LatestBarsResponse getLatestBars(GetLatestMarketDataRequest request);

    LatestQuotesResponse getLatestQuotes(GetLatestMarketDataRequest request);

    SnapshotsResponse getSnapshots(GetLatestMarketDataRequest request);
}
