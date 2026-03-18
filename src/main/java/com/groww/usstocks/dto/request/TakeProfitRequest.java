package com.groww.usstocks.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nested take-profit configuration for bracket orders.
 * Specifies the limit price at which to take profit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TakeProfitRequest {

    @JsonProperty("limit_price")
    private String limitPrice;
}
