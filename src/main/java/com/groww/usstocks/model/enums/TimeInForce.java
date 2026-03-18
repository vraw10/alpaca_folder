package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the time-in-force for an order.
 * For US Stocks MVP, we only allow DAY orders as per requirement.
 */
public enum TimeInForce {

    DAY("day"),
    GTC("gtc"),       // Good Til Cancelled
    OPG("opg"),       // Market on Open
    CLS("cls"),       // Market on Close
    IOC("ioc"),       // Immediate or Cancel
    FOK("fok");       // Fill or Kill

    private final String value;

    TimeInForce(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

