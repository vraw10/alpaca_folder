package com.groww.usstocks.controller;

import java.util.List;
import java.util.Map;

import com.groww.usstocks.dto.request.CreateOrderRequest;
import com.groww.usstocks.dto.request.GetOrdersRequest;
import com.groww.usstocks.dto.request.ReplaceOrderRequest;
import com.groww.usstocks.dto.response.EstimateOrderResponse;
import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.model.enums.OrderQueryStatus;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.SortDirection;
import com.groww.usstocks.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for order operations.
 * Account ID is read from the {@code X-Account-Id} header.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Place a new order.
     * POST /api/v1/orders
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Get all orders with optional filters.
     * GET /api/v1/orders?status=open&limit=50&direction=desc&side=buy
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @RequestParam(required = false) OrderQueryStatus status,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String after,
            @RequestParam(required = false) String until,
            @RequestParam(required = false) SortDirection direction,
            @RequestParam(required = false) Boolean nested,
            @RequestParam(required = false) String symbols,
            @RequestParam(required = false) OrderSide side) {
        GetOrdersRequest params = GetOrdersRequest.builder()
                .status(status)
                .limit(limit)
                .after(after)
                .until(until)
                .direction(direction)
                .nested(nested)
                .symbols(symbols)
                .side(side)
                .build();
        return ResponseEntity.ok(orderService.getOrders(accountId, params));
    }

    /**
     * Get a single order by its broker order ID.
     * GET /api/v1/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(accountId, orderId));
    }

    /**
     * Get a single order by client_order_id (idempotency key).
     * GET /api/v1/orders/by-client-id?clientOrderId=xxx
     */
    @GetMapping("/by-client-id")
    public ResponseEntity<OrderResponse> getOrderByClientOrderId(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @RequestParam String clientOrderId) {
        return ResponseEntity.ok(orderService.getOrderByClientOrderId(accountId, clientOrderId));
    }

    /**
     * Replace/modify an existing order.
     * PATCH /api/v1/orders/{orderId}
     */
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponse> replaceOrder(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @PathVariable String orderId,
            @Valid @RequestBody ReplaceOrderRequest request) {
        return ResponseEntity.ok(orderService.replaceOrder(accountId, orderId, request));
    }

    /**
     * Cancel a single open order.
     * DELETE /api/v1/orders/{orderId}
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> cancelOrder(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @PathVariable String orderId) {
        orderService.cancelOrder(accountId, orderId);
        return ResponseEntity.ok(Map.of("message", "Order cancellation requested successfully."));
    }

    /**
     * Cancel ALL open orders.
     * DELETE /api/v1/orders
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> cancelAllOrders(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId) {
        orderService.cancelAllOrders(accountId);
        return ResponseEntity.ok(Map.of("message", "All order cancellations requested successfully."));
    }

    /**
     * Estimate an order without placing it.
     * POST /api/v1/orders/estimate
     */
    @PostMapping("/estimate")
    public ResponseEntity<EstimateOrderResponse> estimateOrder(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.estimateOrder(accountId, request));
    }
}
