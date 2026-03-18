package com.groww.usstocks.marketdata.alpaca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca trade data point (used inside snapshots).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaTrade {

    /** Timestamp (RFC3339). */
    @JsonProperty("t")
    private String timestamp;

    /** Exchange. */
    @JsonProperty("x")
    private String exchange;

    /** Price. */
    @JsonProperty("p")
    private Double price;

    /** Size. */
    @JsonProperty("s")
    private Long size;

    /** Conditions. */
    @JsonProperty("c")
    private java.util.List<String> conditions;
}
