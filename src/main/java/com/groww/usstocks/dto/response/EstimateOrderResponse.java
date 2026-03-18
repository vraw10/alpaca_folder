package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for order estimation.
 * POST /v1/trading/accounts/{account_id}/orders/estimation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EstimateOrderResponse {

    private String symbol;
    private String qty;
    private String side;
    private String type;

    @JsonProperty("time_in_force")
    private String timeInForce;

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    @JsonProperty("estimated_price")
    private String estimatedPrice;

    @JsonProperty("buying_power_change")
    private String buyingPowerChange;
}

