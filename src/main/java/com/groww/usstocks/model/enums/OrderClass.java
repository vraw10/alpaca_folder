package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Order class — determines whether the order is simple or part of a
 * multi-leg group (bracket, OCO, OTO, mleg).
 */
public enum OrderClass {

    SIMPLE("simple"),
    BRACKET("bracket"),
    OCO("oco"),
    OTO("oto"),
    MLEG("mleg");

    private final String value;

    OrderClass(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
