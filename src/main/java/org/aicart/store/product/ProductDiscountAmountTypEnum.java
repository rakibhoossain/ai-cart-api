package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductDiscountAmountTypEnum {
    FIXED("fixed"),
    PERCENTAGE("percentage");

    private final String value;

    ProductDiscountAmountTypEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
