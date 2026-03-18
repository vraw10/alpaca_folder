package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Asset class classification.
 */
public enum AssetClass {

    US_EQUITY("us_equity"),
    CRYPTO("crypto");

    private final String value;

    AssetClass(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
