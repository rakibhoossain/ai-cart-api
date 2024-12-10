package org.aicart;

public enum PaymentTypeEnum {
    CASH_ON_DELIVERY("COD"),
    STRIPE("STRIPE"),
    SSLCOMMERZ("SSLZ");

    private final String value;

    PaymentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
