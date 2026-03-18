package com.groww.usstocks.service;

import java.util.Collections;
import java.util.List;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.dto.request.GetMarketCalendarRequest;
import com.groww.usstocks.dto.response.CalendarDayResponse;
import com.groww.usstocks.exception.RequestValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MarketCalendarServiceTest {

    private BrokerService brokerService;
    private MarketCalendarService marketCalendarService;

    @BeforeEach
    void setUp() {
        brokerService = mock(BrokerService.class);
        marketCalendarService = new MarketCalendarService(brokerService);
    }

    // ── Date validation ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Date validation")
    class DateValidation {

        @Test
        @DisplayName("start after end → RequestValidationException")
        void shouldRejectStartAfterEnd() {
            GetMarketCalendarRequest params = GetMarketCalendarRequest.builder()
                    .start("2026-03-15")
                    .end("2026-03-01")
                    .build();
            assertThrows(RequestValidationException.class,
                    () -> marketCalendarService.getMarketCalendar(params));
        }

        @ParameterizedTest(name = "Invalid date ''{0}'' in ''{1}'' field")
        @CsvSource({
                "not-a-date, start",
                "2026-13-01, start",
                "20260301,   start",
                "not-a-date, end",
                "2026-00-15, end"
        })
        void shouldRejectInvalidDateFormat(String badDate, String field) {
            GetMarketCalendarRequest.GetMarketCalendarRequestBuilder builder =
                    GetMarketCalendarRequest.builder();
            if ("start".equals(field)) {
                builder.start(badDate);
            } else {
                builder.end(badDate);
            }
            assertThrows(RequestValidationException.class,
                    () -> marketCalendarService.getMarketCalendar(builder.build()));
        }

        @Test
        @DisplayName("start equals end → valid (single day)")
        void shouldAllowStartEqualsEnd() {
            when(brokerService.getMarketCalendar(any())).thenReturn(Collections.emptyList());
            GetMarketCalendarRequest params = GetMarketCalendarRequest.builder()
                    .start("2026-03-15")
                    .end("2026-03-15")
                    .build();
            List<CalendarDayResponse> result = marketCalendarService.getMarketCalendar(params);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Only start provided → valid")
        void shouldAllowOnlyStart() {
            when(brokerService.getMarketCalendar(any())).thenReturn(Collections.emptyList());
            GetMarketCalendarRequest params = GetMarketCalendarRequest.builder()
                    .start("2026-01-01")
                    .build();
            List<CalendarDayResponse> result = marketCalendarService.getMarketCalendar(params);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Only end provided → valid")
        void shouldAllowOnlyEnd() {
            when(brokerService.getMarketCalendar(any())).thenReturn(Collections.emptyList());
            GetMarketCalendarRequest params = GetMarketCalendarRequest.builder()
                    .end("2026-12-31")
                    .build();
            List<CalendarDayResponse> result = marketCalendarService.getMarketCalendar(params);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Null params → delegates without error")
        void shouldDelegateWithNullParams() {
            when(brokerService.getMarketCalendar(null)).thenReturn(Collections.emptyList());
            List<CalendarDayResponse> result = marketCalendarService.getMarketCalendar(null);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ── Delegation ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Delegation to BrokerService")
    class Delegation {

        @Test
        @DisplayName("Passes params through to broker")
        void shouldDelegateToBrokerService() {
            CalendarDayResponse day = CalendarDayResponse.builder()
                    .date("2026-01-02").open("09:30").close("16:00").build();
            when(brokerService.getMarketCalendar(any())).thenReturn(List.of(day));

            GetMarketCalendarRequest params = GetMarketCalendarRequest.builder()
                    .start("2026-01-01").end("2026-01-31").build();
            List<CalendarDayResponse> result = marketCalendarService.getMarketCalendar(params);

            assertEquals(1, result.size());
            assertEquals("2026-01-02", result.get(0).getDate());
            verify(brokerService).getMarketCalendar(params);
        }

        @Test
        @DisplayName("Null broker response → empty list")
        void shouldReturnEmptyListForNullBrokerResponse() {
            when(brokerService.getMarketCalendar(any())).thenReturn(null);
            GetMarketCalendarRequest params = GetMarketCalendarRequest.builder().build();
            List<CalendarDayResponse> result = marketCalendarService.getMarketCalendar(params);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
