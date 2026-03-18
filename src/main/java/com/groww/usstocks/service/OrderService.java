package com.groww.usstocks.service;

import java.util.Collections;
import java.util.List;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.dto.request.CreateOrderRequest;
import com.groww.usstocks.dto.request.GetOrdersRequest;
import com.groww.usstocks.dto.request.ReplaceOrderRequest;
import com.groww.usstocks.dto.response.EstimateOrderResponse;
import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.exception.RequestValidationException;
import com.groww.usstocks.validation.AccountIdValidator;
import com.groww.usstocks.validation.OrderValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for order operations.
 * Applies validation and business rules, then delegates to the
 * vendor-agnostic {@link BrokerService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final BrokerService brokerService;
    private final AccountIdValidator accountIdValidator;
    private final OrderValidator orderValidator;

    public OrderResponse createOrder(String accountId, CreateOrderRequest request) {
        accountIdValidator.validate(accountId);
        orderValidator.validateCreateOrder(request);

        log.info("Placing order: account={}, symbol={}, side={}, type={}, qty={}, notional={}",
                accountId, request.getSymbol(), request.getSide(), request.getType(),
                request.getQty(), request.getNotional());

        return brokerService.createOrder(accountId, request);
    }

    public List<OrderResponse> getOrders(String accountId, GetOrdersRequest params) {
        accountIdValidator.validate(accountId);
        List<OrderResponse> orders = brokerService.getOrders(accountId, params);
        return orders != null ? orders : Collections.emptyList();
    }

    public OrderResponse getOrderById(String accountId, String orderId) {
        accountIdValidator.validate(accountId);
        validateOrderId(orderId);
        return brokerService.getOrderById(accountId, orderId);
    }

    public OrderResponse getOrderByClientOrderId(String accountId, String clientOrderId) {
        accountIdValidator.validate(accountId);
        if (clientOrderId == null || clientOrderId.isBlank()) {
            throw new RequestValidationException("client_order_id must not be blank.");
        }
        return brokerService.getOrderByClientOrderId(accountId, clientOrderId);
    }

    public OrderResponse replaceOrder(String accountId, String orderId, ReplaceOrderRequest request) {
        accountIdValidator.validate(accountId);
        validateOrderId(orderId);
        log.info("Replacing order={} for account={}", orderId, accountId);
        return brokerService.replaceOrder(accountId, orderId, request);
    }

    public void cancelOrder(String accountId, String orderId) {
        accountIdValidator.validate(accountId);
        validateOrderId(orderId);
        log.info("Cancelling order={} for account={}", orderId, accountId);
        brokerService.cancelOrder(accountId, orderId);
    }

    public void cancelAllOrders(String accountId) {
        accountIdValidator.validate(accountId);
        log.info("Cancelling all orders for account={}", accountId);
        brokerService.cancelAllOrders(accountId);
    }

    public EstimateOrderResponse estimateOrder(String accountId, CreateOrderRequest request) {
        accountIdValidator.validate(accountId);
        orderValidator.validateCreateOrder(request);
        return brokerService.estimateOrder(accountId, request);
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new RequestValidationException("order_id must not be blank.");
        }
    }
}
