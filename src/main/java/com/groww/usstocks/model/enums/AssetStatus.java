package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status of a tradeable asset.
 */
public enum AssetStatus {

    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    AssetStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
