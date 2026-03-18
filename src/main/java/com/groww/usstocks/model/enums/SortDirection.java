package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Sort direction for list queries.
 */
public enum SortDirection {

    ASC("asc"),
    DESC("desc");

    private final String value;

    SortDirection(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

