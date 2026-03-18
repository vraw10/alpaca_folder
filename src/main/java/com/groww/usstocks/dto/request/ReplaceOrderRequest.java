package com.groww.usstocks.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groww.usstocks.model.enums.TimeInForce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for replacing/modifying an existing order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceOrderRequest {

    private String qty;

    @JsonProperty("time_in_force")
    private TimeInForce timeInForce;

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    private String trail;

    @JsonProperty("client_order_id")
    private String clientOrderId;
}
