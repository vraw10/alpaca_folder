package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Stock exchange where an asset is listed.
 */
public enum Exchange {

    AMEX("AMEX"),
    ARCA("ARCA"),
    BATS("BATS"),
    NYSE("NYSE"),
    NASDAQ("NASDAQ"),
    NYSEARCA("NYSEARCA"),
    OTC("OTC");

    private final String value;

    Exchange(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
