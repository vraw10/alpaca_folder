package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal, vendor-agnostic response representing a single market calendar day.
 * <p>
 * Contains market open/close times and settlement information.
 * Early closure days will have a different {@code close} time (e.g. "13:00").
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarDayResponse {

    /** Trading date in YYYY-MM-DD format. */
    private String date;

    /** Market open time in HH:MM format (e.g. "09:30"). */
    private String open;

    /** Market close time in HH:MM format (e.g. "16:00", or "13:00" for early close). */
    private String close;

    /** Extended/pre-market session open time in HHMM format (e.g. "0400"). */
    @JsonProperty("session_open")
    private String sessionOpen;

    /** Extended/after-market session close time in HHMM format (e.g. "2000"). */
    @JsonProperty("session_close")
    private String sessionClose;

    /** Settlement date for trades on this day, in YYYY-MM-DD format. */
    @JsonProperty("settlement_date")
    private String settlementDate;
}
