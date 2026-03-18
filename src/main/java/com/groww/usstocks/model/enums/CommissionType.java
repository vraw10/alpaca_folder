package com.groww.usstocks.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * How the commission amount should be interpreted.
 * <ul>
 *   <li>{@code notional} — flat dollar fee per order</li>
 *   <li>{@code qty} — fee per share/contract</li>
 *   <li>{@code bps} — basis points on order notional</li>
 * </ul>
 */
public enum CommissionType {

    NOTIONAL("notional"),
    QTY("qty"),
    BPS("bps");

    private final String value;

    CommissionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
