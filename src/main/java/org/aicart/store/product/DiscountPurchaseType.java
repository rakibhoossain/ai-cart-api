package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscountPurchaseType {

    ONE_TIME("one-time"),
    SUBSCRIPTION("subscription"),
    BOTH("both");

    private final String value;

    DiscountPurchaseType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
