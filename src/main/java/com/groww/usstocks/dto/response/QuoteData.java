package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vendor-agnostic quote (bid/ask) data point.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteData {

    private String timestamp;

    @JsonProperty("ask_exchange")
    private String askExchange;

    @JsonProperty("ask_price")
    private Double askPrice;

    @JsonProperty("ask_size")
    private Long askSize;

    @JsonProperty("bid_exchange")
    private String bidExchange;

    @JsonProperty("bid_price")
    private Double bidPrice;

    @JsonProperty("bid_size")
    private Long bidSize;
}
