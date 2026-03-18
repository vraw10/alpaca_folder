package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the side of a trade order.
 */
public enum OrderSide {

    BUY("buy"),
    SELL("sell");

    private final String value;

    OrderSide(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

