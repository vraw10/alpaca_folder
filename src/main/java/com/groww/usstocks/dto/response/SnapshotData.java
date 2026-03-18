package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vendor-agnostic stock snapshot — composite of latest trade, quote, and bars.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnapshotData {

    @JsonProperty("latest_trade")
    private TradeData latestTrade;

    @JsonProperty("latest_quote")
    private QuoteData latestQuote;

    @JsonProperty("minute_bar")
    private BarData minuteBar;

    @JsonProperty("daily_bar")
    private BarData dailyBar;

    @JsonProperty("prev_daily_bar")
    private BarData prevDailyBar;
}
