package com.groww.usstocks.marketdata.alpaca;

import java.util.Map;

import com.groww.usstocks.dto.request.GetLatestMarketDataRequest;
import com.groww.usstocks.dto.request.GetStockBarsRequest;
import com.groww.usstocks.dto.response.LatestBarsResponse;
import com.groww.usstocks.dto.response.LatestQuotesResponse;
import com.groww.usstocks.dto.response.SnapshotsResponse;
import com.groww.usstocks.dto.response.StockBarsResponse;
import com.groww.usstocks.exception.MarketDataApiException;
import com.groww.usstocks.marketdata.MarketDataService;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaBarsResponse;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaLatestBarsResponse;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaLatestQuotesResponse;
import com.groww.usstocks.marketdata.alpaca.dto.AlpacaSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Alpaca-specific implementation of {@link MarketDataService}.
 * <p>
 * Uses the Alpaca Market Data API with API key authentication.
 * Separate from the Broker API (different base URL and auth mechanism).
 */
@Slf4j
@Service
public class AlpacaMarketDataService implements MarketDataService {

    private static final String BARS_PATH = "/v2/stocks/bars";
    private static final String LATEST_BARS_PATH = "/v2/stocks/bars/latest";
    private static final String LATEST_QUOTES_PATH = "/v2/stocks/quotes/latest";
    private static final String SNAPSHOTS_PATH = "/v2/stocks/snapshots";

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final AlpacaMarketDataResponseMapper mapper;

    public AlpacaMarketDataService(RestTemplate alpacaMarketDataRestTemplate,
                                    AlpacaMarketDataConfig config,
                                    AlpacaMarketDataResponseMapper mapper) {
        this.restTemplate = alpacaMarketDataRestTemplate;
        this.baseUrl = config.getBaseUrl();
        this.mapper = mapper;
    }

    // ── Historical Bars ─────────────────────────────────────────────────

    @Override
    public StockBarsResponse getHistoricalBars(GetStockBarsRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + BARS_PATH);
        if (request != null) {
            if (request.getSymbols() != null) uriBuilder.queryParam("symbols", request.getSymbols());
            if (request.getTimeframe() != null) uriBuilder.queryParam("timeframe", request.getTimeframe());
            if (request.getStart() != null) uriBuilder.queryParam("start", request.getStart());
            if (request.getEnd() != null) uriBuilder.queryParam("end", request.getEnd());
            if (request.getLimit() != null) uriBuilder.queryParam("limit", request.getLimit());
            if (request.getPageToken() != null) uriBuilder.queryParam("page_token", request.getPageToken());
            if (request.getSort() != null) uriBuilder.queryParam("sort", request.getSort());
            if (request.getAdjustment() != null) uriBuilder.queryParam("adjustment", request.getAdjustment());
            if (request.getFeed() != null) uriBuilder.queryParam("feed", request.getFeed());
        }
        String url = uriBuilder.build().toUriString();
        log.debug("Alpaca: fetching historical bars");
        AlpacaBarsResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null, AlpacaBarsResponse.class
        ), "fetch historical bars").getBody();
        return mapper.toStockBarsResponse(raw);
    }

    // ── Latest Bars ─────────────────────────────────────────────────────

    @Override
    public LatestBarsResponse getLatestBars(GetLatestMarketDataRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + LATEST_BARS_PATH);
        addCommonMarketDataParams(uriBuilder, request);
        String url = uriBuilder.build().toUriString();
        log.debug("Alpaca: fetching latest bars");
        AlpacaLatestBarsResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null, AlpacaLatestBarsResponse.class
        ), "fetch latest bars").getBody();
        return mapper.toLatestBarsResponse(raw);
    }

    // ── Latest Quotes ───────────────────────────────────────────────────

    @Override
    public LatestQuotesResponse getLatestQuotes(GetLatestMarketDataRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + LATEST_QUOTES_PATH);
        addCommonMarketDataParams(uriBuilder, request);
        String url = uriBuilder.build().toUriString();
        log.debug("Alpaca: fetching latest quotes");
        AlpacaLatestQuotesResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null, AlpacaLatestQuotesResponse.class
        ), "fetch latest quotes").getBody();
        return mapper.toLatestQuotesResponse(raw);
    }

    // ── Snapshots ───────────────────────────────────────────────────────

    @Override
    public SnapshotsResponse getSnapshots(GetLatestMarketDataRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + SNAPSHOTS_PATH);
        addCommonMarketDataParams(uriBuilder, request);
        String url = uriBuilder.build().toUriString();
        log.debug("Alpaca: fetching snapshots");
        Map<String, AlpacaSnapshot> raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, AlpacaSnapshot>>() {}
        ), "fetch snapshots").getBody();
        return mapper.toSnapshotsResponse(raw);
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private void addCommonMarketDataParams(UriComponentsBuilder uriBuilder,
                                            GetLatestMarketDataRequest request) {
        if (request != null) {
            if (request.getSymbols() != null) uriBuilder.queryParam("symbols", request.getSymbols());
            if (request.getFeed() != null) uriBuilder.queryParam("feed", request.getFeed());
        }
    }

    private <T> ResponseEntity<T> execute(RestCall<T> call, String operation) {
        try {
            return call.execute();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Alpaca Market Data API error during {}: status={}", operation, ex.getStatusCode());
            throw new MarketDataApiException("Failed to " + operation, ex.getStatusCode());
        } catch (RestClientException ex) {
            log.error("Alpaca Market Data connection error during {}", operation);
            throw new MarketDataApiException("Failed to " + operation + " - connection error", ex);
        }
    }

    @FunctionalInterface
    private interface RestCall<T> {
        ResponseEntity<T> execute();
    }
}
