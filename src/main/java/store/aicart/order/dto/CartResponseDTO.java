package store.aicart.order.dto;

import java.util.List;

public class CartResponseDTO {
    private Long cartId;
    private String currency = "USD";
    private List<CartItemDTO> cartItems;
    private OrderBillingDTO billing;
    private OrderShippingDTO shipping;
    private boolean useShippingAsBilling;

    public CartResponseDTO(Long cartId, List<CartItemDTO> cartItems, String currency) {
        this.cartId = cartId;
        this.cartItems = cartItems;
        this.currency = currency;
    }


    public CartResponseDTO(Long cartId, List<CartItemDTO> cartItems, String currency, OrderBillingDTO billing, OrderShippingDTO shipping) {
        this.cartId = cartId;
        this.cartItems = cartItems;
        this.currency = currency;
        this.setAddressData(billing, shipping);
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
}
