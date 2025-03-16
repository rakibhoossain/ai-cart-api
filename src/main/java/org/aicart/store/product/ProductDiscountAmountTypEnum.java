package org.aicart.store.product;

public enum ProductDiscountAmountTypEnum {
    FIXED("fixed"),
    PERCENTAGE("percentage");

    private final String value;

    ProductDiscountAmountTypEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
