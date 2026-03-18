package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Controls whether calendar queries filter by trading date or settlement date.
 * <p>
 * Alpaca expects uppercase values for this parameter.
 */
public enum DateType {

    TRADING("TRADING"),
    SETTLEMENT("SETTLEMENT");

    private final String value;

    DateType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
