package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCollectionTypeEnum {
    MANUAL("manual"),
    SMART("smart");

    private final String value;

    ProductCollectionTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
