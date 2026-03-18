package com.groww.usstocks.broker.alpaca;

import java.util.List;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.broker.alpaca.dto.AlpacaAssetResponse;
import com.groww.usstocks.broker.alpaca.dto.AlpacaCalendarDay;
import com.groww.usstocks.broker.alpaca.dto.AlpacaOrderResponse;
import com.groww.usstocks.dto.request.CreateOrderRequest;
import com.groww.usstocks.dto.request.GetAssetsRequest;
import com.groww.usstocks.dto.request.GetMarketCalendarRequest;
import com.groww.usstocks.dto.request.GetOrdersRequest;
import com.groww.usstocks.dto.request.ReplaceOrderRequest;
import com.groww.usstocks.dto.response.AccountResponse;
import com.groww.usstocks.dto.response.AssetResponse;
import com.groww.usstocks.dto.response.CalendarDayResponse;
import com.groww.usstocks.dto.response.EstimateOrderResponse;
import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.dto.response.PositionResponse;
import com.groww.usstocks.dto.response.TradingLimitsResponse;
import com.groww.usstocks.exception.BrokerApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Alpaca-specific implementation of {@link BrokerService}.
 * <p>
 * Deserialises into Alpaca-specific DTOs ({@link AlpacaOrderResponse}),
 * then maps to our internal, vendor-agnostic DTOs via
 * {@link AlpacaResponseMapper}.
 */
@Slf4j
@Service
public class AlpacaBrokerService implements BrokerService {

    private static final String TRADING_BASE = "/v1/trading/accounts/{account_id}";
    private static final String ORDERS_PATH = TRADING_BASE + "/orders";
    private static final String ORDER_BY_ID_PATH = ORDERS_PATH + "/{order_id}";
    private static final String ORDER_BY_CLIENT_ID_PATH = ORDERS_PATH + ":by_client_order_id";
    private static final String ORDER_ESTIMATION_PATH = ORDERS_PATH + "/estimation";
    private static final String POSITIONS_PATH = TRADING_BASE + "/positions";
    private static final String POSITION_BY_SYMBOL_PATH = POSITIONS_PATH + "/{symbol_or_asset_id}";
    private static final String TRADING_LIMITS_PATH = TRADING_BASE + "/limits";
    private static final String ACCOUNT_PATH = "/v1/accounts/{account_id}";
    private static final String ASSETS_PATH = "/v1/assets";
    private static final String ASSET_BY_SYMBOL_PATH = ASSETS_PATH + "/{symbol_or_asset_id}";
    private static final String CALENDAR_PATH = "/v1/calendar";

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final AlpacaResponseMapper mapper;

    public AlpacaBrokerService(RestTemplate alpacaRestTemplate,
                               AlpacaApiConfig config,
                               AlpacaResponseMapper mapper) {
        this.restTemplate = alpacaRestTemplate;
        this.baseUrl = config.getBaseUrl();
        this.mapper = mapper;
    }

    // ── Orders ───────────────────────────────────────────────────────────

    @Override
    public OrderResponse createOrder(String accountId, CreateOrderRequest request) {
        String url = baseUrl + ORDERS_PATH;
        log.debug("Alpaca: creating order for account={}, symbol={}", accountId, request.getSymbol());
        AlpacaOrderResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(request),
                AlpacaOrderResponse.class, accountId
        ), "create order").getBody();
        return mapper.toOrderResponse(raw);
    }

    @Override
    public List<OrderResponse> getOrders(String accountId, GetOrdersRequest params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + ORDERS_PATH);
        if (params != null) {
            if (params.getStatus() != null) uriBuilder.queryParam("status", params.getStatus().getValue());
            if (params.getLimit() != null) uriBuilder.queryParam("limit", params.getLimit());
            if (params.getAfter() != null) uriBuilder.queryParam("after", params.getAfter());
            if (params.getUntil() != null) uriBuilder.queryParam("until", params.getUntil());
            if (params.getDirection() != null) uriBuilder.queryParam("direction", params.getDirection().getValue());
            if (params.getNested() != null) uriBuilder.queryParam("nested", params.getNested());
            if (params.getSymbols() != null) uriBuilder.queryParam("symbols", params.getSymbols());
            if (params.getSide() != null) uriBuilder.queryParam("side", params.getSide().getValue());
        }
        String url = uriBuilder.buildAndExpand(accountId).toUriString();
        log.debug("Alpaca: fetching orders for account={}", accountId);
        List<AlpacaOrderResponse> rawList = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<AlpacaOrderResponse>>() {}
        ), "fetch orders").getBody();
        return mapper.toOrderResponseList(rawList);
    }

    @Override
    public OrderResponse getOrderById(String accountId, String orderId) {
        String url = baseUrl + ORDER_BY_ID_PATH;
        log.debug("Alpaca: fetching order={} for account={}", orderId, accountId);
        AlpacaOrderResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                AlpacaOrderResponse.class, accountId, orderId
        ), "fetch order by ID").getBody();
        return mapper.toOrderResponse(raw);
    }

    @Override
    public OrderResponse getOrderByClientOrderId(String accountId, String clientOrderId) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + ORDER_BY_CLIENT_ID_PATH)
                .queryParam("client_order_id", clientOrderId)
                .buildAndExpand(accountId)
                .toUriString();
        log.debug("Alpaca: fetching order by clientOrderId={} for account={}", clientOrderId, accountId);
        AlpacaOrderResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null, AlpacaOrderResponse.class
        ), "fetch order by client order ID").getBody();
        return mapper.toOrderResponse(raw);
    }

    @Override
    public OrderResponse replaceOrder(String accountId, String orderId, ReplaceOrderRequest request) {
        String url = baseUrl + ORDER_BY_ID_PATH;
        log.debug("Alpaca: replacing order={} for account={}", orderId, accountId);
        AlpacaOrderResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.PATCH, new HttpEntity<>(request),
                AlpacaOrderResponse.class, accountId, orderId
        ), "replace order").getBody();
        return mapper.toOrderResponse(raw);
    }

    @Override
    public void cancelOrder(String accountId, String orderId) {
        String url = baseUrl + ORDER_BY_ID_PATH;
        log.debug("Alpaca: cancelling order={} for account={}", orderId, accountId);
        execute(() -> restTemplate.exchange(
                url, HttpMethod.DELETE, null,
                Void.class, accountId, orderId
        ), "cancel order");
    }

    @Override
    public void cancelAllOrders(String accountId) {
        String url = baseUrl + ORDERS_PATH;
        log.debug("Alpaca: cancelling all orders for account={}", accountId);
        execute(() -> restTemplate.exchange(
                url, HttpMethod.DELETE, null,
                Void.class, accountId
        ), "cancel all orders");
    }

    @Override
    public EstimateOrderResponse estimateOrder(String accountId, CreateOrderRequest request) {
        String url = baseUrl + ORDER_ESTIMATION_PATH;
        log.debug("Alpaca: estimating order for account={}, symbol={}", accountId, request.getSymbol());
        return execute(() -> restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(request),
                EstimateOrderResponse.class, accountId
        ), "estimate order").getBody();
    }

    // ── Positions ────────────────────────────────────────────────────────

    @Override
    public List<PositionResponse> getPositions(String accountId) {
        String url = baseUrl + POSITIONS_PATH;
        log.debug("Alpaca: fetching positions for account={}", accountId);
        return execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<PositionResponse>>() {}, accountId
        ), "fetch positions").getBody();
    }

    @Override
    public PositionResponse getPositionBySymbolOrId(String accountId, String symbolOrAssetId) {
        String url = baseUrl + POSITION_BY_SYMBOL_PATH;
        log.debug("Alpaca: fetching position={} for account={}", symbolOrAssetId, accountId);
        return execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                PositionResponse.class, accountId, symbolOrAssetId
        ), "fetch position").getBody();
    }

    // ── Account ──────────────────────────────────────────────────────────

    @Override
    public AccountResponse getAccount(String accountId) {
        String url = baseUrl + ACCOUNT_PATH;
        log.debug("Alpaca: fetching account={}", accountId);
        return execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                AccountResponse.class, accountId
        ), "fetch account").getBody();
    }

    @Override
    public TradingLimitsResponse getTradingLimits(String accountId) {
        String url = baseUrl + TRADING_LIMITS_PATH;
        log.debug("Alpaca: fetching trading limits for account={}", accountId);
        return execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                TradingLimitsResponse.class, accountId
        ), "fetch trading limits").getBody();
    }

    // ── Close Position ─────────────────────────────────────────────────

    @Override
    public OrderResponse closePosition(String accountId, String symbolOrAssetId) {
        String url = baseUrl + POSITION_BY_SYMBOL_PATH;
        log.debug("Alpaca: closing position={} for account={}", symbolOrAssetId, accountId);
        AlpacaOrderResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.DELETE, null,
                AlpacaOrderResponse.class, accountId, symbolOrAssetId
        ), "close position").getBody();
        return mapper.toOrderResponse(raw);
    }

    // ── Assets ──────────────────────────────────────────────────────────

    @Override
    public List<AssetResponse> getAssets(GetAssetsRequest params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + ASSETS_PATH);
        if (params != null) {
            if (params.getStatus() != null) uriBuilder.queryParam("status", params.getStatus().getValue());
            if (params.getAssetClass() != null) uriBuilder.queryParam("asset_class", params.getAssetClass().getValue());
            if (params.getExchange() != null) uriBuilder.queryParam("exchange", params.getExchange().getValue());
        }
        String url = uriBuilder.build().toUriString();
        log.debug("Alpaca: fetching assets");
        List<AlpacaAssetResponse> rawList = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<AlpacaAssetResponse>>() {}
        ), "fetch assets").getBody();
        return mapper.toAssetResponseList(rawList);
    }

    @Override
    public AssetResponse getAssetBySymbolOrId(String symbolOrAssetId) {
        String url = baseUrl + ASSET_BY_SYMBOL_PATH;
        log.debug("Alpaca: fetching asset={}", symbolOrAssetId);
        AlpacaAssetResponse raw = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                AlpacaAssetResponse.class, symbolOrAssetId
        ), "fetch asset").getBody();
        return mapper.toAssetResponse(raw);
    }

    // ── Calendar ─────────────────────────────────────────────────────────

    @Override
    public List<CalendarDayResponse> getMarketCalendar(GetMarketCalendarRequest params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + CALENDAR_PATH);
        if (params != null) {
            if (params.getStart() != null) uriBuilder.queryParam("start", params.getStart());
            if (params.getEnd() != null) uriBuilder.queryParam("end", params.getEnd());
            if (params.getDateType() != null) uriBuilder.queryParam("date_type", params.getDateType().getValue());
        }
        String url = uriBuilder.build().toUriString();
        log.debug("Alpaca: fetching market calendar");
        List<AlpacaCalendarDay> rawList = execute(() -> restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<AlpacaCalendarDay>>() {}
        ), "fetch market calendar").getBody();
        return mapper.toCalendarDayResponseList(rawList);
    }

    // ── Helper ───────────────────────────────────────────────────────────

    private <T> ResponseEntity<T> execute(RestCall<T> call, String operation) {
        try {
            return call.execute();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Alpaca API error during {}: status={}, body={}",
                    operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new BrokerApiException("Failed to " + operation, ex.getStatusCode());
        } catch (RestClientException ex) {
            log.error("Alpaca connection error during {}", operation);
            throw new BrokerApiException("Failed to " + operation + " - connection error", ex);
        }
    }

    @FunctionalInterface
    private interface RestCall<T> {
        ResponseEntity<T> execute();
    }
}
