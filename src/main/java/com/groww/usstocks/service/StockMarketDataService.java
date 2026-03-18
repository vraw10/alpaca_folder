package com.groww.usstocks.service;

import com.groww.usstocks.dto.request.GetLatestMarketDataRequest;
import com.groww.usstocks.dto.request.GetStockBarsRequest;
import com.groww.usstocks.dto.response.LatestBarsResponse;
import com.groww.usstocks.dto.response.LatestQuotesResponse;
import com.groww.usstocks.dto.response.SnapshotsResponse;
import com.groww.usstocks.dto.response.StockBarsResponse;
import com.groww.usstocks.exception.RequestValidationException;
import com.groww.usstocks.marketdata.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for stock market data operations.
 * Validates symbols parameter, then delegates to the vendor-agnostic
 * {@link MarketDataService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketDataService {

    private final MarketDataService marketDataService;

    public StockBarsResponse getHistoricalBars(GetStockBarsRequest request) {
        validateSymbols(request.getSymbols());
        return marketDataService.getHistoricalBars(request);
    }

    public LatestBarsResponse getLatestBars(GetLatestMarketDataRequest request) {
        validateSymbols(request.getSymbols());
        return marketDataService.getLatestBars(request);
    }

    public LatestQuotesResponse getLatestQuotes(GetLatestMarketDataRequest request) {
        validateSymbols(request.getSymbols());
        return marketDataService.getLatestQuotes(request);
    }

    public SnapshotsResponse getSnapshots(GetLatestMarketDataRequest request) {
        validateSymbols(request.getSymbols());
        return marketDataService.getSnapshots(request);
    }

    private void validateSymbols(String symbols) {
        if (symbols == null || symbols.isBlank()) {
            throw new RequestValidationException("symbols parameter must not be blank.");
        }
    }
}
