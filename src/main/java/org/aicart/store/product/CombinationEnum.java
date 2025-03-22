package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CombinationEnum {
    PRODUCT_DISCOUNT("product_discount"),
    ORDER_DISCOUNT("order_discount"),
    SHIPPING_DISCOUNT("shipping_discount");

    private final String value;

    CombinationEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
