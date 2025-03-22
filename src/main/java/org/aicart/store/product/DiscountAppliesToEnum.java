package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscountAppliesToEnum {
    PRODUCT("product"),
    COLLECTION("collection");

    private final String value;

    DiscountAppliesToEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
