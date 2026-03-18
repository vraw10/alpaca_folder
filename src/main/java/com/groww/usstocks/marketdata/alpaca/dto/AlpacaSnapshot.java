package com.groww.usstocks.marketdata.alpaca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca snapshot — composite of latest trade, quote, and bars.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaSnapshot {

    @JsonProperty("latestTrade")
    private AlpacaTrade latestTrade;

    @JsonProperty("latestQuote")
    private AlpacaQuote latestQuote;

    @JsonProperty("minuteBar")
    private AlpacaBar minuteBar;

    @JsonProperty("dailyBar")
    private AlpacaBar dailyBar;

    @JsonProperty("prevDailyBar")
    private AlpacaBar prevDailyBar;
}
