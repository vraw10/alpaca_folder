package com.groww.usstocks.broker.alpaca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw response DTO from Alpaca Broker API for the calendar endpoint.
 * <p>
 * This is internal to the Alpaca broker package. It is mapped to the
 * public {@link com.groww.usstocks.dto.response.CalendarDayResponse} via
 * {@link com.groww.usstocks.broker.alpaca.AlpacaResponseMapper}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaCalendarDay {

    /** Trading date in YYYY-MM-DD format. */
    private String date;

    /** Market open time in HH:MM format. */
    private String open;

    /** Market close time in HH:MM format. */
    private String close;

    /** Extended session open time in HHMM format. */
    @JsonProperty("session_open")
    private String sessionOpen;

    /** Extended session close time in HHMM format. */
    @JsonProperty("session_close")
    private String sessionClose;

    /** Settlement date in YYYY-MM-DD format. */
    @JsonProperty("settlement_date")
    private String settlementDate;
}
