package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the type of order.
 * For US Stocks MVP, we primarily use MARKET and LIMIT.
 */
public enum OrderType {

    MARKET("market"),
    LIMIT("limit"),
    STOP("stop"),
    STOP_LIMIT("stop_limit"),
    TRAILING_STOP("trailing_stop");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

