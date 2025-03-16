package org.aicart.store.product;

public enum DiscountAppliesToEnum {
    PRODUCT("product"),
    COLLECTION("collection");

    private final String value;

    DiscountAppliesToEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
