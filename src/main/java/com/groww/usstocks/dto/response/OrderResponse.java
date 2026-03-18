package com.groww.usstocks.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.CommissionType;
import com.groww.usstocks.model.enums.OrderClass;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.OrderStatus;
import com.groww.usstocks.model.enums.OrderType;
import com.groww.usstocks.model.enums.PositionIntent;
import com.groww.usstocks.model.enums.TimeInForce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal order response — decoupled from any third-party broker.
 * <p>
 * All enum fields use our own enums (not raw broker strings).
 * The broker-specific mapping happens in the respective mapper
 * (e.g. {@code AlpacaResponseMapper}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

    /** Our internal order ID (maps from broker's order UUID). */
    private String id;

    /** Client-provided idempotency key. */
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
    private AssetClass assetClass;

    private String notional;

    private String qty;

    @JsonProperty("filled_qty")
    private String filledQty;

    @JsonProperty("filled_avg_price")
    private String filledAvgPrice;

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    @JsonProperty("extended_hours")
    private Boolean extendedHours;

    @JsonProperty("trail_price")
    private String trailPrice;

    @JsonProperty("trail_percent")
    private String trailPercent;

    // ── Enum fields — our own types, not broker-specific ──

    /** Our internal order status. */
    private OrderStatus status;

    /** Buy or sell. */
    private OrderSide side;

    /** Order type: market, limit, stop, etc. */
    private OrderType type;

    /** Time in force: day, gtc, etc. */
    @JsonProperty("time_in_force")
    private TimeInForce timeInForce;

    // ── Commission ──────────────────────────────────────────────────────

    /** Flat fee charged to the end customer. */
    private String commission;

    /** How the commission is interpreted: notional, qty, or bps. */
    @JsonProperty("commission_type")
    private CommissionType commissionType;

    // ── Order class + legs ──────────────────────────────────────────────

    /** simple, bracket, oco, oto, mleg. */
    @JsonProperty("order_class")
    private OrderClass orderClass;

    /** Sub-orders for bracket / OCO / OTO / mleg orders. */
    private List<OrderResponse> legs;

    // ── Trailing stop ───────────────────────────────────────────────────

    /** High water mark for trailing-stop orders. */
    private String hwm;

    // ── Position intent ─────────────────────────────────────────────────

    /** buy_to_open, buy_to_close, sell_to_open, sell_to_close. */
    @JsonProperty("position_intent")
    private PositionIntent positionIntent;
}
