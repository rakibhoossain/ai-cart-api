package store.aicart.order.dto;

import java.math.BigInteger;
import java.util.List;

public class CartResponseDTO {
    private Long cartId;
    private String currency = "USD";
    private List<CartItemDTO> cartItems;

    public CartResponseDTO(Long cartId, List<CartItemDTO> cartItems, String currency) {
        this.cartId = cartId;
        this.cartItems = cartItems;
        this.currency = currency;
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
}
