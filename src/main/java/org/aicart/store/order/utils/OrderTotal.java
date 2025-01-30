package org.aicart.store.order.utils;

import java.math.BigInteger;

public class OrderTotal {
    public BigInteger subTotal;
    public BigInteger totalTax;
    public BigInteger shippingCost;
    public BigInteger totalDiscount;
    public BigInteger totalPrice;

    public OrderTotal(BigInteger subTotal, BigInteger totalTax, BigInteger shippingCost, BigInteger totalDiscount, BigInteger totalPrice) {
        this.subTotal = subTotal;
        this.totalTax = totalTax;
        this.shippingCost = shippingCost;
        this.totalDiscount = totalDiscount;
        this.totalPrice = totalPrice;
    }
}
