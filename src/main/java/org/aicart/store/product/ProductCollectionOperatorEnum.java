package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCollectionOperatorEnum {
    EQUALS("equals"),
    NOT_EQUALS("not_equals"),
    CONTAINS("contains"),
    GREATER_THAN("greater_than"),
    LESS_THAN("less_than");

    private final String value;

    ProductCollectionOperatorEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
