package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status filter used when querying a list of orders.
 * <p>
 * This is NOT the same as {@link OrderStatus} — this controls
 * which orders are returned (open / closed / all).
 * <p>
 * See: <a href="https://docs.alpaca.markets/reference/getallorders-1">Alpaca GET orders</a>
 */
public enum OrderQueryStatus {

    /** Only open (active) orders. */
    OPEN("open"),

    /** Only closed (filled, cancelled, expired, etc.) orders. */
    CLOSED("closed"),

    /** All orders regardless of status. */
    ALL("all");

    private final String value;

    OrderQueryStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

