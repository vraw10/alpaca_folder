package com.groww.usstocks.marketdata.alpaca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca bar (OHLCV) data point.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaBar {

    /** Timestamp (RFC3339). */
    @JsonProperty("t")
    private String timestamp;

    /** Open price. */
    @JsonProperty("o")
    private Double open;

    /** High price. */
    @JsonProperty("h")
    private Double high;

    /** Low price. */
    @JsonProperty("l")
    private Double low;

    /** Close price. */
    @JsonProperty("c")
    private Double close;

    /** Volume. */
    @JsonProperty("v")
    private Long volume;

    /** Number of trades. */
    @JsonProperty("n")
    private Long tradeCount;

    /** Volume-weighted average price. */
    @JsonProperty("vw")
    private Double vwap;
}
