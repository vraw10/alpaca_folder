package com.groww.usstocks.marketdata.alpaca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca quote (bid/ask) data point.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaQuote {

    /** Timestamp (RFC3339). */
    @JsonProperty("t")
    private String timestamp;

    /** Ask exchange. */
    @JsonProperty("ax")
    private String askExchange;

    /** Ask price. */
    @JsonProperty("ap")
    private Double askPrice;

    /** Ask size. */
    @JsonProperty("as")
    private Long askSize;

    /** Bid exchange. */
    @JsonProperty("bx")
    private String bidExchange;

    /** Bid price. */
    @JsonProperty("bp")
    private Double bidPrice;

    /** Bid size. */
    @JsonProperty("bs")
    private Long bidSize;

    /** Conditions. */
    @JsonProperty("c")
    private java.util.List<String> conditions;
}
