package org.aicart.store.product;

public enum ProductDiscountEnum {

    PRODUCT_DISCOUNT("moneyOffProduct"),
    BY_X_GET_Y("buyXgetY"),
    ORDER_DISCOUNT("moneyOffOrder"),
    SHIPPING_DISCOUNT("shipping");

    private final String value;

    ProductDiscountEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
