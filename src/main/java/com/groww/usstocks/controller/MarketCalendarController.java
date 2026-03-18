package com.groww.usstocks.controller;

import java.util.List;

import com.groww.usstocks.dto.request.GetMarketCalendarRequest;
import com.groww.usstocks.dto.response.CalendarDayResponse;
import com.groww.usstocks.model.enums.DateType;
import com.groww.usstocks.service.MarketCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the US market calendar.
 * No authentication required — calendar data is publicly available.
 */
@RestController
@RequestMapping("/api/v1/market-data/calendar")
@RequiredArgsConstructor
public class MarketCalendarController {

    private final MarketCalendarService marketCalendarService;

    /**
     * Get US market trading days within a date range.
     * <p>
     * GET /api/v1/market-data/calendar?start=2026-01-01&end=2026-03-31&date_type=TRADING
     */
    @GetMapping
    public ResponseEntity<List<CalendarDayResponse>> getMarketCalendar(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(value = "date_type", required = false) DateType dateType) {
        GetMarketCalendarRequest request = GetMarketCalendarRequest.builder()
                .start(start)
                .end(end)
                .dateType(dateType)
                .build();
        return ResponseEntity.ok(marketCalendarService.getMarketCalendar(request));
    }
}
