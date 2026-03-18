package com.groww.usstocks.controller;

import com.groww.usstocks.dto.request.GetLatestMarketDataRequest;
import com.groww.usstocks.dto.request.GetStockBarsRequest;
import com.groww.usstocks.dto.response.LatestBarsResponse;
import com.groww.usstocks.dto.response.LatestQuotesResponse;
import com.groww.usstocks.dto.response.SnapshotsResponse;
import com.groww.usstocks.dto.response.StockBarsResponse;
import com.groww.usstocks.service.StockMarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for stock market data operations.
 * No authentication required — market data is publicly available.
 */
@RestController
@RequestMapping("/api/v1/market-data/stocks")
@RequiredArgsConstructor
public class MarketDataController {

    private final StockMarketDataService stockMarketDataService;

    /**
     * Get historical OHLCV bars.
     * GET /api/v1/market-data/stocks/bars?symbols=AAPL,MSFT&timeframe=1Day&start=...&end=...
     */
    @GetMapping("/bars")
    public ResponseEntity<StockBarsResponse> getHistoricalBars(
            @RequestParam String symbols,
            @RequestParam(required = false) String timeframe,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Integer limit,
            @RequestParam(value = "page_token", required = false) String pageToken,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String adjustment,
            @RequestParam(required = false) String feed) {
        GetStockBarsRequest request = GetStockBarsRequest.builder()
                .symbols(symbols)
                .timeframe(timeframe)
                .start(start)
                .end(end)
                .limit(limit)
                .pageToken(pageToken)
                .sort(sort)
                .adjustment(adjustment)
                .feed(feed)
                .build();
        return ResponseEntity.ok(stockMarketDataService.getHistoricalBars(request));
    }

    /**
     * Get the latest bar per symbol.
     * GET /api/v1/market-data/stocks/bars/latest?symbols=AAPL,MSFT
     */
    @GetMapping("/bars/latest")
    public ResponseEntity<LatestBarsResponse> getLatestBars(
            @RequestParam String symbols,
            @RequestParam(required = false) String feed) {
        GetLatestMarketDataRequest request = GetLatestMarketDataRequest.builder()
                .symbols(symbols)
                .feed(feed)
                .build();
        return ResponseEntity.ok(stockMarketDataService.getLatestBars(request));
    }

    /**
     * Get the latest bid/ask quote per symbol.
     * GET /api/v1/market-data/stocks/quotes/latest?symbols=AAPL,MSFT
     */
    @GetMapping("/quotes/latest")
    public ResponseEntity<LatestQuotesResponse> getLatestQuotes(
            @RequestParam String symbols,
            @RequestParam(required = false) String feed) {
        GetLatestMarketDataRequest request = GetLatestMarketDataRequest.builder()
                .symbols(symbols)
                .feed(feed)
                .build();
        return ResponseEntity.ok(stockMarketDataService.getLatestQuotes(request));
    }

    /**
     * Get full snapshot (latest trade, quote, minute/daily bars) per symbol.
     * GET /api/v1/market-data/stocks/snapshots?symbols=AAPL,MSFT
     */
    @GetMapping("/snapshots")
    public ResponseEntity<SnapshotsResponse> getSnapshots(
            @RequestParam String symbols,
            @RequestParam(required = false) String feed) {
        GetLatestMarketDataRequest request = GetLatestMarketDataRequest.builder()
                .symbols(symbols)
                .feed(feed)
                .build();
        return ResponseEntity.ok(stockMarketDataService.getSnapshots(request));
    }
}
