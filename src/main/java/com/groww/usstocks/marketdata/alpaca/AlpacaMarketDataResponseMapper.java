package com.groww.usstocks.marketdata.alpaca;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.groww.usstocks.dto.response.BarData;
import com.groww.usstocks.dto.response.LatestBarsResponse;
import com.groww.usstocks.dto.response.LatestQuotesResponse;
import com.groww.usstocks.dto.response.QuoteData;
import com.groww.usstocks.dto.response.SnapshotData;
import com.groww.usstocks.dto.response.SnapshotsResponse;
import com.groww.usstocks.dto.response.StockBarsResponse;
import com.groww.usstocks.dto.response.TradeData;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaBar;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaBarsResponse;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaLatestBarsResponse;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaLatestQuotesResponse;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaQuote;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaSnapshot;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaTrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Maps Alpaca market data DTOs to vendor-agnostic response DTOs.
 */
@Slf4j
@Component
public class AlpacaMarketDataResponseMapper {

    // ── Historical Bars ─────────────────────────────────────────────────

    public StockBarsResponse toStockBarsResponse(AlpacaBarsResponse raw) {
        if (raw == null) {
            return StockBarsResponse.builder()
                    .bars(Collections.emptyMap())
                    .build();
        }
        Map<String, List<BarData>> mappedBars = new HashMap<>();
        if (raw.getBars() != null) {
            raw.getBars().forEach((symbol, barList) ->
                    mappedBars.put(symbol, barList.stream().map(this::toBarData).toList())
            );
        }
        return StockBarsResponse.builder()
                .bars(mappedBars)
                .nextPageToken(raw.getNextPageToken())
                .build();
    }

    // ── Latest Bars ─────────────────────────────────────────────────────

    public LatestBarsResponse toLatestBarsResponse(AlpacaLatestBarsResponse raw) {
        if (raw == null) {
            return LatestBarsResponse.builder()
                    .bars(Collections.emptyMap())
                    .build();
        }
        Map<String, BarData> mappedBars = new HashMap<>();
        if (raw.getBars() != null) {
            raw.getBars().forEach((symbol, bar) ->
                    mappedBars.put(symbol, toBarData(bar))
            );
        }
        return LatestBarsResponse.builder()
                .bars(mappedBars)
                .build();
    }

    // ── Latest Quotes ───────────────────────────────────────────────────

    public LatestQuotesResponse toLatestQuotesResponse(AlpacaLatestQuotesResponse raw) {
        if (raw == null) {
            return LatestQuotesResponse.builder()
                    .quotes(Collections.emptyMap())
                    .build();
        }
        Map<String, QuoteData> mappedQuotes = new HashMap<>();
        if (raw.getQuotes() != null) {
            raw.getQuotes().forEach((symbol, quote) ->
                    mappedQuotes.put(symbol, toQuoteData(quote))
            );
        }
        return LatestQuotesResponse.builder()
                .quotes(mappedQuotes)
                .build();
    }

    // ── Snapshots ───────────────────────────────────────────────────────

    public SnapshotsResponse toSnapshotsResponse(Map<String, AlpacaSnapshot> raw) {
        if (raw == null) {
            return SnapshotsResponse.builder()
                    .snapshots(Collections.emptyMap())
                    .build();
        }
        Map<String, SnapshotData> mappedSnapshots = new HashMap<>();
        raw.forEach((symbol, snapshot) ->
                mappedSnapshots.put(symbol, toSnapshotData(snapshot))
        );
        return SnapshotsResponse.builder()
                .snapshots(mappedSnapshots)
                .build();
    }

    // ── Internal mapping helpers ────────────────────────────────────────

    public BarData toBarData(AlpacaBar raw) {
        if (raw == null) {
            return null;
        }
        return BarData.builder()
                .timestamp(raw.getTimestamp())
                .open(raw.getOpen())
                .high(raw.getHigh())
                .low(raw.getLow())
                .close(raw.getClose())
                .volume(raw.getVolume())
                .tradeCount(raw.getTradeCount())
                .vwap(raw.getVwap())
                .build();
    }

    public QuoteData toQuoteData(AlpacaQuote raw) {
        if (raw == null) {
            return null;
        }
        return QuoteData.builder()
                .timestamp(raw.getTimestamp())
                .askExchange(raw.getAskExchange())
                .askPrice(raw.getAskPrice())
                .askSize(raw.getAskSize())
                .bidExchange(raw.getBidExchange())
                .bidPrice(raw.getBidPrice())
                .bidSize(raw.getBidSize())
                .build();
    }

    public TradeData toTradeData(AlpacaTrade raw) {
        if (raw == null) {
            return null;
        }
        return TradeData.builder()
                .timestamp(raw.getTimestamp())
                .exchange(raw.getExchange())
                .price(raw.getPrice())
                .size(raw.getSize())
                .build();
    }

    public SnapshotData toSnapshotData(AlpacaSnapshot raw) {
        if (raw == null) {
            return null;
        }
        return SnapshotData.builder()
                .latestTrade(toTradeData(raw.getLatestTrade()))
                .latestQuote(toQuoteData(raw.getLatestQuote()))
                .minuteBar(toBarData(raw.getMinuteBar()))
                .dailyBar(toBarData(raw.getDailyBar()))
                .prevDailyBar(toBarData(raw.getPrevDailyBar()))
                .build();
    }
}
