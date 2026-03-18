package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Internal order status — decoupled from any third-party broker.
 * <p>
 * Each broker implementation maps its vendor-specific statuses to these values.
 */
public enum OrderStatus {

    /** Order accepted but not yet sent to market. */
    PENDING("pending"),

    /** Order is live on the exchange, waiting for a fill. */
    OPEN("open"),

    /** Part of the order has been filled. */
    PARTIALLY_FILLED("partially_filled"),

    /** Order is fully executed. */
    FILLED("filled"),

    /** Order was cancelled (by user or system). */
    CANCELLED("cancelled"),

    /** Order expired before it could be filled. */
    EXPIRED("expired"),

    /** Order was replaced by a new order (modify). */
    REPLACED("replaced"),

    /** Order was rejected by the exchange or broker. */
    REJECTED("rejected"),

    /** Order is being processed for cancellation. */
    PENDING_CANCEL("pending_cancel"),

    /** Order is being processed for replacement. */
    PENDING_REPLACE("pending_replace"),

    /** Order completed for the day but not fully filled. */
    DONE_FOR_DAY("done_for_day"),

    /** Order is held and under review. */
    HELD("held"),

    /** Status could not be mapped — should be investigated. */
    UNKNOWN("unknown");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
