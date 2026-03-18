package com.groww.usstocks.broker.alpaca.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw response DTO from Alpaca Broker API for order endpoints.
 * <p>
 * This is internal to the Alpaca broker package. It is mapped to the
 * public {@link com.groww.usstocks.dto.response.OrderResponse} via
 * {@link com.groww.usstocks.broker.alpaca.AlpacaResponseMapper}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaOrderResponse {

    private String id;

    @JsonProperty("client_order_id")
    private String clientOrderId;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("submitted_at")
    private String submittedAt;

    @JsonProperty("filled_at")
    private String filledAt;

    @JsonProperty("expired_at")
    private String expiredAt;

    @JsonProperty("canceled_at")
    private String canceledAt;

    @JsonProperty("failed_at")
    private String failedAt;

    @JsonProperty("replaced_at")
    private String replacedAt;

    @JsonProperty("replaced_by")
    private String replacedBy;

    private String replaces;

    @JsonProperty("asset_id")
    private String assetId;

    private String symbol;

    @JsonProperty("asset_class")
    private String assetClass;

    private String notional;

    private String qty;

    @JsonProperty("filled_qty")
    private String filledQty;

    @JsonProperty("filled_avg_price")
    private String filledAvgPrice;

    @JsonProperty("order_class")
    private String orderClass;

    /** Alpaca sometimes returns both 'type' and 'order_type'. */
    private String type;

    @JsonProperty("order_type")
    private String orderType;

    private String side;

    @JsonProperty("time_in_force")
    private String timeInForce;

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    /** Alpaca-native status string (e.g. "new", "accepted", "pending_new"). */
    private String status;

    @JsonProperty("extended_hours")
    private Boolean extendedHours;

    @JsonProperty("trail_price")
    private String trailPrice;

    @JsonProperty("trail_percent")
    private String trailPercent;

    @JsonProperty("hwm")
    private String hwm;

    private String commission;

    @JsonProperty("commission_type")
    private String commissionType;

    @JsonProperty("position_intent")
    private String positionIntent;

    /** Sub-orders for bracket / OCO / OTO / mleg orders. */
    private List<AlpacaOrderResponse> legs;
}

