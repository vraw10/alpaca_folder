package com.groww.usstocks.broker.alpaca;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.groww.usstocks.broker.alpaca.dto.AlpacaCalendarDay;
import com.groww.usstocks.broker.alpaca.dto.AlpacaOrderResponse;
import com.groww.usstocks.dto.response.CalendarDayResponse;
import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.CommissionType;
import com.groww.usstocks.model.enums.OrderClass;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.OrderStatus;
import com.groww.usstocks.model.enums.OrderType;
import com.groww.usstocks.model.enums.PositionIntent;
import com.groww.usstocks.model.enums.TimeInForce;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlpacaResponseMapperTest {

    private AlpacaResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AlpacaResponseMapper();
    }

    // ── Status mapping ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca status → internal OrderStatus mapping")
    class StatusMapping {

        @ParameterizedTest(name = "Alpaca ''{0}'' → OrderStatus.{1}")
        @CsvSource({
                "new,           OPEN",
                "accepted,      OPEN",
                "pending_new,   PENDING",
                "accepted_for_bidding, PENDING",
                "pending_review, PENDING",
                "calculated,    PENDING",
                "partially_filled, PARTIALLY_FILLED",
                "filled,        FILLED",
                "canceled,      CANCELLED",
                "expired,       EXPIRED",
                "replaced,      REPLACED",
                "rejected,      REJECTED",
                "stopped,       REJECTED",
                "suspended,     REJECTED",
                "pending_cancel, PENDING_CANCEL",
                "pending_replace, PENDING_REPLACE",
                "done_for_day,  DONE_FOR_DAY",
                "held,          HELD"
        })
        void shouldMapKnownAlpacaStatuses(String alpacaStatus, OrderStatus expected) {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().status(alpacaStatus).build();
            OrderResponse mapped = mapper.toOrderResponse(raw);
            assertEquals(expected, mapped.getStatus());
        }

        @Test
        @DisplayName("Unknown status → UNKNOWN")
        void shouldMapUnknownStatusToUnknown() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().status("some_future_status").build();
            OrderResponse mapped = mapper.toOrderResponse(raw);
            assertEquals(OrderStatus.UNKNOWN, mapped.getStatus());
        }

        @Test
        @DisplayName("Null status → UNKNOWN")
        void shouldMapNullStatusToUnknown() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().status(null).build();
            OrderResponse mapped = mapper.toOrderResponse(raw);
            assertEquals(OrderStatus.UNKNOWN, mapped.getStatus());
        }
    }

    // ── Side mapping ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca side → internal OrderSide mapping")
    class SideMapping {

        @Test
        void shouldMapBuy() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().side("buy").build();
            assertEquals(OrderSide.BUY, mapper.toOrderResponse(raw).getSide());
        }

        @Test
        void shouldMapSell() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().side("sell").build();
            assertEquals(OrderSide.SELL, mapper.toOrderResponse(raw).getSide());
        }

        @Test
        void shouldReturnNullForNullSide() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().side(null).build();
            assertNull(mapper.toOrderResponse(raw).getSide());
        }
    }

    // ── Type mapping ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca type → internal OrderType mapping")
    class TypeMapping {

        @ParameterizedTest(name = "Alpaca ''{0}'' → OrderType.{1}")
        @CsvSource({
                "market,        MARKET",
                "limit,         LIMIT",
                "stop,          STOP",
                "stop_limit,    STOP_LIMIT",
                "trailing_stop, TRAILING_STOP"
        })
        void shouldMapKnownTypes(String alpacaType, OrderType expected) {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().type(alpacaType).build();
            assertEquals(expected, mapper.toOrderResponse(raw).getType());
        }

        @Test
        @DisplayName("Prefers 'type' over 'order_type' when both set")
        void shouldPreferTypeOverOrderType() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder()
                    .type("limit")
                    .orderType("market")
                    .build();
            assertEquals(OrderType.LIMIT, mapper.toOrderResponse(raw).getType());
        }

        @Test
        @DisplayName("Falls back to 'order_type' when 'type' is null")
        void shouldFallBackToOrderType() {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder()
                    .type(null)
                    .orderType("stop")
                    .build();
            assertEquals(OrderType.STOP, mapper.toOrderResponse(raw).getType());
        }
    }

    // ── TimeInForce mapping ─────────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca time_in_force → internal TimeInForce mapping")
    class TimeInForceMapping {

        @ParameterizedTest(name = "Alpaca ''{0}'' → TimeInForce.{1}")
        @CsvSource({
                "day, DAY",
                "gtc, GTC",
                "opg, OPG",
                "cls, CLS",
                "ioc, IOC",
                "fok, FOK"
        })
        void shouldMapKnownTimeInForce(String alpacaTif, TimeInForce expected) {
            AlpacaOrderResponse raw = AlpacaOrderResponse.builder().timeInForce(alpacaTif).build();
            assertEquals(expected, mapper.toOrderResponse(raw).getTimeInForce());
        }
    }

    // ── Full object mapping ─────────────────────────────────────────────

    @Test
    @DisplayName("Full AlpacaOrderResponse maps all scalar fields")
    void shouldMapAllScalarFields() {
        AlpacaOrderResponse raw = AlpacaOrderResponse.builder()
                .id("order-uuid-123")
                .clientOrderId("my-client-id")
                .createdAt("2026-03-10T08:00:00Z")
                .updatedAt("2026-03-10T08:01:00Z")
                .submittedAt("2026-03-10T08:00:01Z")
                .filledAt("2026-03-10T08:00:05Z")
                .assetId("asset-uuid")
                .symbol("AAPL")
                .assetClass("us_equity")
                .qty("10")
                .filledQty("10")
                .filledAvgPrice("175.50")
                .limitPrice("176.00")
                .stopPrice(null)
                .extendedHours(false)
                .status("filled")
                .side("buy")
                .type("limit")
                .timeInForce("day")
                .build();

        OrderResponse result = mapper.toOrderResponse(raw);

        assertEquals("order-uuid-123", result.getId());
        assertEquals("my-client-id", result.getClientOrderId());
        assertEquals("2026-03-10T08:00:00Z", result.getCreatedAt());
        assertEquals("AAPL", result.getSymbol());
        assertEquals(AssetClass.US_EQUITY, result.getAssetClass());
        assertEquals("10", result.getQty());
        assertEquals("10", result.getFilledQty());
        assertEquals("175.50", result.getFilledAvgPrice());
        assertEquals("176.00", result.getLimitPrice());
        assertEquals(false, result.getExtendedHours());
        assertEquals(OrderStatus.FILLED, result.getStatus());
        assertEquals(OrderSide.BUY, result.getSide());
        assertEquals(OrderType.LIMIT, result.getType());
        assertEquals(TimeInForce.DAY, result.getTimeInForce());
    }

    // ── Null / empty list mapping ───────────────────────────────────────

    @Test
    @DisplayName("Null raw input → null response")
    void shouldReturnNullForNullInput() {
        assertNull(mapper.toOrderResponse(null));
    }

    @Test
    @DisplayName("Null list → empty list")
    void shouldReturnEmptyListForNullInput() {
        List<OrderResponse> result = mapper.toOrderResponseList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Empty list → empty list")
    void shouldReturnEmptyListForEmptyInput() {
        List<OrderResponse> result = mapper.toOrderResponseList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Multiple raw orders → mapped list")
    void shouldMapMultipleOrders() {
        List<AlpacaOrderResponse> rawList = Arrays.asList(
                AlpacaOrderResponse.builder().id("1").status("new").side("buy").type("market").build(),
                AlpacaOrderResponse.builder().id("2").status("filled").side("sell").type("limit").build()
        );
        List<OrderResponse> result = mapper.toOrderResponseList(rawList);

        assertEquals(2, result.size());
        assertEquals(OrderStatus.OPEN, result.get(0).getStatus());
        assertEquals(OrderStatus.FILLED, result.get(1).getStatus());
    }

    // ── CommissionType mapping ───────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca commission_type → internal CommissionType mapping")
    class CommissionTypeMapping {

        @ParameterizedTest(name = "Alpaca ''{0}'' → CommissionType.{1}")
        @CsvSource({"notional, NOTIONAL", "qty, QTY", "bps, BPS"})
        void shouldMapKnownCommissionTypes(String raw, CommissionType expected) {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().commissionType(raw).build();
            assertEquals(expected, mapper.toOrderResponse(r).getCommissionType());
        }

        @Test
        @DisplayName("Null commission_type → null")
        void shouldReturnNullForNullCommissionType() {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().build();
            assertNull(mapper.toOrderResponse(r).getCommissionType());
        }
    }

    // ── OrderClass mapping ───────────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca order_class → internal OrderClass mapping")
    class OrderClassMapping {

        @ParameterizedTest(name = "Alpaca ''{0}'' → OrderClass.{1}")
        @CsvSource({"simple, SIMPLE", "bracket, BRACKET", "oco, OCO", "oto, OTO", "mleg, MLEG"})
        void shouldMapKnownOrderClasses(String raw, OrderClass expected) {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().orderClass(raw).build();
            assertEquals(expected, mapper.toOrderResponse(r).getOrderClass());
        }

        @Test
        @DisplayName("Null order_class → null")
        void shouldReturnNullForNullOrderClass() {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().build();
            assertNull(mapper.toOrderResponse(r).getOrderClass());
        }
    }

    // ── PositionIntent mapping ───────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca position_intent → internal PositionIntent mapping")
    class PositionIntentMapping {

        @ParameterizedTest(name = "Alpaca ''{0}'' → PositionIntent.{1}")
        @CsvSource({
                "buy_to_open, BUY_TO_OPEN",
                "buy_to_close, BUY_TO_CLOSE",
                "sell_to_open, SELL_TO_OPEN",
                "sell_to_close, SELL_TO_CLOSE"
        })
        void shouldMapKnownPositionIntents(String raw, PositionIntent expected) {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().positionIntent(raw).build();
            assertEquals(expected, mapper.toOrderResponse(r).getPositionIntent());
        }

        @Test
        @DisplayName("Null position_intent → null")
        void shouldReturnNullForNullPositionIntent() {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().build();
            assertNull(mapper.toOrderResponse(r).getPositionIntent());
        }
    }

    // ── Legs mapping ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Alpaca legs → recursive OrderResponse mapping")
    class LegsMapping {

        @Test
        @DisplayName("Parent with 2 legs → recursively mapped")
        void shouldMapLegsRecursively() {
            AlpacaOrderResponse leg1 = AlpacaOrderResponse.builder()
                    .id("leg-1").status("new").side("buy").type("limit").build();
            AlpacaOrderResponse leg2 = AlpacaOrderResponse.builder()
                    .id("leg-2").status("new").side("sell").type("stop").build();
            AlpacaOrderResponse parent = AlpacaOrderResponse.builder()
                    .id("parent").orderClass("bracket")
                    .legs(List.of(leg1, leg2)).build();

            OrderResponse result = mapper.toOrderResponse(parent);

            assertNotNull(result.getLegs());
            assertEquals(2, result.getLegs().size());
            assertEquals("leg-1", result.getLegs().get(0).getId());
            assertEquals(OrderSide.BUY, result.getLegs().get(0).getSide());
            assertEquals("leg-2", result.getLegs().get(1).getId());
            assertEquals(OrderSide.SELL, result.getLegs().get(1).getSide());
        }

        @Test
        @DisplayName("Null legs → null")
        void shouldReturnNullLegsWhenAbsent() {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().id("x").build();
            assertNull(mapper.toOrderResponse(r).getLegs());
        }
    }

    // ── HWM and commission passthrough ───────────────────────────────────

    @Nested
    @DisplayName("Scalar passthrough: hwm, commission")
    class ScalarPassthrough {

        @Test
        @DisplayName("hwm and commission map through unchanged")
        void shouldMapHwmAndCommission() {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder()
                    .hwm("155.00").commission("1.50").build();
            OrderResponse result = mapper.toOrderResponse(r);
            assertEquals("155.00", result.getHwm());
            assertEquals("1.50", result.getCommission());
        }

        @Test
        @DisplayName("Null hwm and commission → null")
        void shouldReturnNullWhenAbsent() {
            AlpacaOrderResponse r = AlpacaOrderResponse.builder().build();
            assertNull(mapper.toOrderResponse(r).getHwm());
            assertNull(mapper.toOrderResponse(r).getCommission());
        }
    }

    // ── CalendarDay mapping ──────────────────────────────────────────────

    @Nested
    @DisplayName("AlpacaCalendarDay → CalendarDayResponse mapping")
    class CalendarDayMapping {

        @Test
        @DisplayName("Full AlpacaCalendarDay maps all fields")
        void shouldMapAllCalendarFields() {
            AlpacaCalendarDay raw = AlpacaCalendarDay.builder()
                    .date("2026-01-02")
                    .open("09:30")
                    .close("16:00")
                    .sessionOpen("0400")
                    .sessionClose("2000")
                    .settlementDate("2026-01-03")
                    .build();

            CalendarDayResponse result = mapper.toCalendarDayResponse(raw);

            assertEquals("2026-01-02", result.getDate());
            assertEquals("09:30", result.getOpen());
            assertEquals("16:00", result.getClose());
            assertEquals("0400", result.getSessionOpen());
            assertEquals("2000", result.getSessionClose());
            assertEquals("2026-01-03", result.getSettlementDate());
        }

        @Test
        @DisplayName("Null raw input → null response")
        void shouldReturnNullForNullInput() {
            assertNull(mapper.toCalendarDayResponse(null));
        }

        @Test
        @DisplayName("Null list → empty list")
        void shouldReturnEmptyListForNullInput() {
            List<CalendarDayResponse> result = mapper.toCalendarDayResponseList(null);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Empty list → empty list")
        void shouldReturnEmptyListForEmptyInput() {
            List<CalendarDayResponse> result = mapper.toCalendarDayResponseList(Collections.emptyList());
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Multiple calendar days → mapped list with early close")
        void shouldMapMultipleCalendarDays() {
            List<AlpacaCalendarDay> rawList = Arrays.asList(
                    AlpacaCalendarDay.builder()
                            .date("2026-01-02").open("09:30").close("16:00").build(),
                    AlpacaCalendarDay.builder()
                            .date("2026-11-27").open("09:30").close("13:00").build()
            );
            List<CalendarDayResponse> result = mapper.toCalendarDayResponseList(rawList);

            assertEquals(2, result.size());
            assertEquals("2026-01-02", result.get(0).getDate());
            assertEquals("16:00", result.get(0).getClose());
            assertEquals("2026-11-27", result.get(1).getDate());
            assertEquals("13:00", result.get(1).getClose());
        }
    }
}

