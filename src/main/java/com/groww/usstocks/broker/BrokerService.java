package com.groww.usstocks.broker;

import java.util.List;

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

/**
 * Vendor-agnostic broker interface.
 * <p>
 * All broker operations are defined here so the implementation
 * (Alpaca, DriveWealth, Interactive Brokers, etc.) can be swapped
 * without touching the service or controller layers.
 */
public interface BrokerService {

    // ── Orders ───────────────────────────────────────────────────────────

    OrderResponse createOrder(String accountId, CreateOrderRequest request);

    List<OrderResponse> getOrders(String accountId, GetOrdersRequest params);

    OrderResponse getOrderById(String accountId, String orderId);

    OrderResponse getOrderByClientOrderId(String accountId, String clientOrderId);

    OrderResponse replaceOrder(String accountId, String orderId, ReplaceOrderRequest request);

    void cancelOrder(String accountId, String orderId);

    void cancelAllOrders(String accountId);

    EstimateOrderResponse estimateOrder(String accountId, CreateOrderRequest request);

    // ── Positions ────────────────────────────────────────────────────────

    List<PositionResponse> getPositions(String accountId);

    PositionResponse getPositionBySymbolOrId(String accountId, String symbolOrAssetId);

    OrderResponse closePosition(String accountId, String symbolOrAssetId);

    // ── Account ──────────────────────────────────────────────────────────

    AccountResponse getAccount(String accountId);

    TradingLimitsResponse getTradingLimits(String accountId);

    // ── Assets ───────────────────────────────────────────────────────────

    List<AssetResponse> getAssets(GetAssetsRequest params);

    AssetResponse getAssetBySymbolOrId(String symbolOrAssetId);

    // ── Calendar ──────────────────────────────────────────────────────────

    List<CalendarDayResponse> getMarketCalendar(GetMarketCalendarRequest params);
}

