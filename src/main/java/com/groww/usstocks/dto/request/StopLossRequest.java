package com.groww.usstocks.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nested stop-loss configuration for bracket orders.
 * Specifies the stop price (and optional limit price for stop-limit legs).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopLossRequest {

    @JsonProperty("stop_price")
    private String stopPrice;

    @JsonProperty("limit_price")
    private String limitPrice;
}
