package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscountEligibilityEnum {
    ALL("all"),
    SEGMENT("segment"),
    SPECIFIC("specific");

    private final String value;

    DiscountEligibilityEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
