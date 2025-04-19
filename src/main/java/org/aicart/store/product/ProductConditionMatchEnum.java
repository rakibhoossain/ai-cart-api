package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductConditionMatchEnum {
    ALL("all"),
    ANY("any");

    private final String value;

    ProductConditionMatchEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
