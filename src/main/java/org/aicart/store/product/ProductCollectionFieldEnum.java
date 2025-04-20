package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCollectionFieldEnum {
    TAG("tag"),
    TITLE("title"),
    PRICE("price"),
    CATEGORY("category");

    private final String value;

    ProductCollectionFieldEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
