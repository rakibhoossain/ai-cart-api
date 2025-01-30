package org.aicart.store.order.dto;

import org.aicart.store.order.entity.Cart;
import java.util.List;

public class CartResponseDTO {
    private Long cartId;
    private int step = 0;
    private String currency = "USD";
    private List<CartItemDTO> cartItems;
    private OrderBillingDTO billing;
    private OrderShippingDTO shipping;
    private boolean useShippingAsBilling;

    private String deliveryMethod;
    private String paymentMethod;

    private Long total;
    private Long subTotal;
    private Long totalTax;
    private Long discount;
    private String couponCode;

    public CartResponseDTO(Long cartId, int step, List<CartItemDTO> cartItems, String currency) {
        this.cartId = cartId;
        this.step = step;
        this.cartItems = cartItems;
        this.currency = currency;
    }


    public CartResponseDTO(Long cartId, int step, List<CartItemDTO> cartItems, String currency, OrderBillingDTO billing, OrderShippingDTO shipping) {
        this.cartId = cartId;
        this.step = step;
        this.cartItems = cartItems;
        this.currency = currency;
        this.setAddressData(billing, shipping);
    }

    public CartResponseDTO(Cart cart, List<CartItemDTO> cartItems) {
        this.cartId = cart.id;
        this.step = cart.step;
        this.cartItems = cartItems;
        this.currency = "EUR"; // TODO:: need to be dynamic
        this.paymentMethod = cart.paymentMethod;
        this.deliveryMethod = cart.deliveryMethod;
        this.couponCode = cart.couponCode;

        this.total = 0L;
        this.subTotal = 0L;
        this.totalTax = 0L;
        this.discount = 0L;

        this.setAddressData(cart.billing, cart.shipping);
    }

    public void setAddressData(OrderBillingDTO billing, OrderShippingDTO shipping) {
        this.billing = billing;
        this.shipping = shipping;
        this.useShippingAsBilling = shipping == null;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItemDTO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public OrderBillingDTO getBilling() {
        return billing;
    }

    public void setBilling(OrderBillingDTO billing) {
        this.billing = billing;
    }

    public OrderShippingDTO getShipping() {
        return shipping;
    }

    public void setShipping(OrderShippingDTO shipping) {
        this.shipping = shipping;
    }

    public boolean isUseShippingAsBilling() {
        return useShippingAsBilling;
    }

    public void setUseShippingAsBilling(boolean useShippingAsBilling) {
        this.useShippingAsBilling = useShippingAsBilling;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Long subTotal) {
        this.subTotal = subTotal;
    }

    public Long getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Long totalTax) {
        this.totalTax = totalTax;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
