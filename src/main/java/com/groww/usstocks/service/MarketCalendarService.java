package com.groww.usstocks.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.dto.request.GetMarketCalendarRequest;
import com.groww.usstocks.dto.response.CalendarDayResponse;
import com.groww.usstocks.exception.RequestValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer for market calendar operations.
 * Validates input dates and delegates to the broker.
 */
@Service
@RequiredArgsConstructor
public class MarketCalendarService {

    private final BrokerService brokerService;

    public List<CalendarDayResponse> getMarketCalendar(GetMarketCalendarRequest params) {
        if (params != null) {
            validateDates(params);
        }
        List<CalendarDayResponse> result = brokerService.getMarketCalendar(params);
        return result != null ? result : Collections.emptyList();
    }

    private void validateDates(GetMarketCalendarRequest params) {
        LocalDate startDate = parseDate(params.getStart(), "start");
        LocalDate endDate = parseDate(params.getEnd(), "end");

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new RequestValidationException(
                    "'start' date must not be after 'end' date. start=" + params.getStart()
                            + ", end=" + params.getEnd());
        }
    }

    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            throw new RequestValidationException(
                    "Invalid '" + fieldName + "' date format. Expected YYYY-MM-DD, got: " + dateStr);
        }
    }
}
