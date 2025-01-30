package org.aicart.store.order.dto;

public class CartAddressRequestDTO {
    private OrderBillingDTO billing;
    private OrderShippingDTO shipping;
    private boolean useShippingAsBilling;

    public CartAddressRequestDTO(OrderBillingDTO billing, OrderShippingDTO shipping, boolean useShippingAsBilling) {
        this.billing = billing;
        this.shipping = shipping;
        this.useShippingAsBilling = useShippingAsBilling;
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
}
