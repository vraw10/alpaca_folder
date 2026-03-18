package com.groww.usstocks.dto.request;

import com.groww.usstocks.model.enums.DateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request parameters for the market calendar endpoint.
 * All fields are optional — omitting them returns the full calendar range.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMarketCalendarRequest {

    /** First date to retrieve (inclusive), in YYYY-MM-DD format. */
    private String start;

    /** Last date to retrieve (inclusive), in YYYY-MM-DD format. */
    private String end;

    /** Whether to filter by trading date or settlement date. Defaults to TRADING. */
    private DateType dateType;
}
