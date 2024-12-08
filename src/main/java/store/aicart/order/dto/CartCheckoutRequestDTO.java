package store.aicart.order.dto;

public class CartCheckoutRequestDTO {
    private OrderBillingDTO billing;
    private OrderShippingDTO shipping;

    public CartCheckoutRequestDTO(OrderBillingDTO billing, OrderShippingDTO shipping) {
        this.billing = billing;
        this.shipping = shipping;
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
}
