package org.aicart.store.product;

public enum DiscountPurchaseType {

    ONE_TIME("one_time"),
    SUBSCRIPTION("subscription"),
    BOTH("both");

    private final String value;

    DiscountPurchaseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
