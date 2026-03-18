package com.groww.usstocks.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groww.usstocks.model.enums.CommissionType;
import com.groww.usstocks.model.enums.OrderClass;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.OrderType;
import com.groww.usstocks.model.enums.PositionIntent;
import com.groww.usstocks.model.enums.TimeInForce;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating an order.
 * <p>
 * Constraints enforced by {@link com.groww.usstocks.validation.OrderValidator}:
 * - time_in_force: only DAY
 * - qty and notional: mutually exclusive (at least one required)
 * - limit_price / stop_price: required for respective order types
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    /** Number of shares. Mutually exclusive with notional. */
    private String qty;

    /** Dollar amount. Mutually exclusive with qty. Only for market orders. */
    private String notional;

    @NotNull(message = "Order side is required")
    private OrderSide side;

    @NotNull(message = "Order type is required")
    private OrderType type;

    @JsonProperty("time_in_force")
    @NotNull(message = "Time in force is required")
    private TimeInForce timeInForce;

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    @JsonProperty("extended_hours")
    private Boolean extendedHours;

    /** Client-generated idempotency key (max 128 chars). */
    @JsonProperty("client_order_id")
    private String clientOrderId;

    // ── Commission ──────────────────────────────────────────────────────

    /** Flat fee to charge the end customer. */
    private String commission;

    /** How the commission amount is interpreted: notional, qty, or bps. */
    @JsonProperty("commission_type")
    private CommissionType commissionType;

    // ── Order class + bracket legs ──────────────────────────────────────

    /** Order class: simple, bracket, oco, oto, mleg. */
    @JsonProperty("order_class")
    private OrderClass orderClass;

    /** Take-profit leg for bracket orders. */
    @JsonProperty("take_profit")
    private TakeProfitRequest takeProfit;

    /** Stop-loss leg for bracket orders. */
    @JsonProperty("stop_loss")
    private StopLossRequest stopLoss;

    // ── Position intent ─────────────────────────────────────────────────

    /** Intent: buy_to_open, buy_to_close, sell_to_open, sell_to_close. */
    @JsonProperty("position_intent")
    private PositionIntent positionIntent;
}
