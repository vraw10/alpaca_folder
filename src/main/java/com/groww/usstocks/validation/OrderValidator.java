package com.groww.usstocks.validation;

import com.groww.usstocks.dto.request.CreateOrderRequest;
import com.groww.usstocks.exception.RequestValidationException;
import com.groww.usstocks.model.enums.OrderClass;
import com.groww.usstocks.model.enums.OrderType;
import com.groww.usstocks.model.enums.TimeInForce;
import org.springframework.stereotype.Component;

/**
 * Validates order requests against business rules and Alpaca API constraints.
 * Enforces our MVP requirements:
 *   - time_in_force must be DAY
 *   - qty and notional are mutually exclusive (at least one required)
 *   - limit_price required for LIMIT and STOP_LIMIT orders
 *   - stop_price required for STOP and STOP_LIMIT orders
 *   - extended_hours only valid with LIMIT + DAY
 *   - notional only valid with MARKET orders
 */
@Component
public class OrderValidator {

    private static final int MAX_CLIENT_ORDER_ID_LENGTH = 128;

    public void validateCreateOrder(CreateOrderRequest request) {
        validateTimeInForce(request);
        validateQtyOrNotional(request);
        validatePricesForOrderType(request);
        validateExtendedHours(request);
        validateClientOrderId(request);
        validateCommission(request);
        validateOrderClass(request);
        validateBracketLegs(request);
    }

    private void validateTimeInForce(CreateOrderRequest request) {
        // MVP requirement: only DAY orders allowed
        if (request.getTimeInForce() != TimeInForce.DAY) {
            throw new RequestValidationException(
                    "Only DAY time_in_force is allowed for US Stocks MVP. Received: " + request.getTimeInForce());
        }
    }

    private void validateQtyOrNotional(CreateOrderRequest request) {
        boolean hasQty = request.getQty() != null && !request.getQty().isBlank();
        boolean hasNotional = request.getNotional() != null && !request.getNotional().isBlank();

        if (!hasQty && !hasNotional) {
            throw new RequestValidationException("Either 'qty' or 'notional' must be provided.");
        }
        if (hasQty && hasNotional) {
            throw new RequestValidationException("'qty' and 'notional' are mutually exclusive. Provide only one.");
        }
        // Notional orders only work with MARKET orders
        if (hasNotional && request.getType() != OrderType.MARKET) {
            throw new RequestValidationException("'notional' orders are only supported for MARKET order type.");
        }
    }

    private void validatePricesForOrderType(CreateOrderRequest request) {
        OrderType type = request.getType();
        boolean hasLimitPrice = request.getLimitPrice() != null && !request.getLimitPrice().isBlank();
        boolean hasStopPrice = request.getStopPrice() != null && !request.getStopPrice().isBlank();

        if (type == OrderType.LIMIT || type == OrderType.STOP_LIMIT) {
            if (!hasLimitPrice) {
                throw new RequestValidationException("'limit_price' is required for " + type.getValue() + " orders.");
            }
        }
        if (type == OrderType.STOP || type == OrderType.STOP_LIMIT) {
            if (!hasStopPrice) {
                throw new RequestValidationException("'stop_price' is required for " + type.getValue() + " orders.");
            }
        }
        if (type == OrderType.MARKET) {
            if (hasLimitPrice || hasStopPrice) {
                throw new RequestValidationException("MARKET orders should not have limit_price or stop_price.");
            }
        }
    }

    private void validateExtendedHours(CreateOrderRequest request) {
        if (Boolean.TRUE.equals(request.getExtendedHours())) {
            if (request.getType() != OrderType.LIMIT) {
                throw new RequestValidationException("extended_hours is only supported for LIMIT orders.");
            }
            if (request.getTimeInForce() != TimeInForce.DAY) {
                throw new RequestValidationException("extended_hours requires time_in_force to be DAY.");
            }
        }
    }

    private void validateClientOrderId(CreateOrderRequest request) {
        if (request.getClientOrderId() != null
                && request.getClientOrderId().length() > MAX_CLIENT_ORDER_ID_LENGTH) {
            throw new RequestValidationException(
                    "client_order_id must not exceed " + MAX_CLIENT_ORDER_ID_LENGTH + " characters.");
        }
    }

    private void validateCommission(CreateOrderRequest request) {
        if (request.getCommissionType() != null
                && (request.getCommission() == null || request.getCommission().isBlank())) {
            throw new RequestValidationException(
                    "'commission' is required when 'commission_type' is provided.");
        }
    }

    private void validateOrderClass(CreateOrderRequest request) {
        if (request.getOrderClass() != null && request.getOrderClass() != OrderClass.SIMPLE) {
            throw new RequestValidationException(
                    "Only 'simple' order_class is allowed for US Stocks MVP. Received: "
                            + request.getOrderClass().getValue());
        }
    }

    private void validateBracketLegs(CreateOrderRequest request) {
        if (request.getTakeProfit() != null) {
            if (request.getTakeProfit().getLimitPrice() == null
                    || request.getTakeProfit().getLimitPrice().isBlank()) {
                throw new RequestValidationException(
                        "take_profit.limit_price is required when take_profit is provided.");
            }
        }
        if (request.getStopLoss() != null) {
            if (request.getStopLoss().getStopPrice() == null
                    || request.getStopLoss().getStopPrice().isBlank()) {
                throw new RequestValidationException(
                        "stop_loss.stop_price is required when stop_loss is provided.");
            }
        }
    }
}

