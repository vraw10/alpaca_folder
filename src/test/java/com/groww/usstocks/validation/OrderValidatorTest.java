package com.groww.usstocks.validation;

import com.groww.usstocks.dto.request.CreateOrderRequest;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.OrderType;
import com.groww.usstocks.model.enums.TimeInForce;
import com.groww.usstocks.exception.RequestValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidatorTest {

    private final OrderValidator validator = new OrderValidator();

    @Nested
    @DisplayName("Tesla LIMIT order — qty=1.4578, price=450")
    class TeslaLimitOrderTest {

        @Test
        @DisplayName("Should pass validation with all required fields (DAY TIF)")
        void shouldPassWithValidLimitOrder() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .symbol("TSLA")
                    .qty("1.4578")
                    .side(OrderSide.BUY)
                    .type(OrderType.LIMIT)
                    .timeInForce(TimeInForce.DAY)
                    .limitPrice("450")
                    .build();

            assertDoesNotThrow(() -> validator.validateCreateOrder(request));
        }

        @Test
        @DisplayName("Should fail if time_in_force is not DAY (e.g. GTC)")
        void shouldFailWithNonDayTif() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .symbol("TSLA")
                    .qty("1.4578")
                    .side(OrderSide.BUY)
                    .type(OrderType.LIMIT)
                    .timeInForce(TimeInForce.GTC)
                    .limitPrice("450")
                    .build();

            RequestValidationException ex = assertThrows(
                    RequestValidationException.class,
                    () -> validator.validateCreateOrder(request));
            assertTrue(ex.getMessage().contains("DAY"));
        }

        @Test
        @DisplayName("Should fail if limit_price is missing for LIMIT order")
        void shouldFailWithoutLimitPrice() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .symbol("TSLA")
                    .qty("1.4578")
                    .side(OrderSide.BUY)
                    .type(OrderType.LIMIT)
                    .timeInForce(TimeInForce.DAY)
                    .build();

            RequestValidationException ex = assertThrows(
                    RequestValidationException.class,
                    () -> validator.validateCreateOrder(request));
            assertTrue(ex.getMessage().contains("limit_price"));
        }
    }
}
