package org.aicart.store.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductDiscountEnum {

    PRODUCT_DISCOUNT("moneyOffProduct"),
    BY_X_GET_Y("buyXgetY"),
    ORDER_DISCOUNT("moneyOffOrder"),
    SHIPPING_DISCOUNT("shipping");

    private final String value;

    ProductDiscountEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }


//    @JsonCreator // Allows JSON input to be mapped using the string value
//    public static ProductDiscountEnum fromValue(String value) {
//        for (ProductDiscountEnum type : values()) {
//            if (type.value.equals(value)) {
//                return type;
//            }
//        }
//        throw new IllegalArgumentException("Invalid discount type: " + value);
//    }
}
