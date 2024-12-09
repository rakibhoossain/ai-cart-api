package store.aicart.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import store.aicart.order.dto.CartAddressRequestDTO;
import store.aicart.order.dto.CartItemDTO;
import store.aicart.order.dto.CartResponseDTO;
import store.aicart.order.entity.Cart;
import store.aicart.order.entity.CartItem;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CartService {

    private final String sessionKey = "cart-session";

    @Inject
    CartRepository cartRepository;

    public CartResponseDTO getCart(String sessionId, Long userId) {
        Cart cart = cartRepository.getCart(sessionId, userId);

        if(cart != null) {
            List<CartItemDTO> cartItems = cartRepository.getCartItems(cart);
            return new CartResponseDTO(cart.id, cartItems, "USD", cart.billing, cart.shipping);
        }

        return null;
    }

    public Cart firstOrCreate(String sessionId, Long userId) {
        Cart cart = cartRepository.getCart(sessionId, userId);

        if(cart == null) {
            return cartRepository.createNewCart(sessionId, null);
        }

        return cart;
    }

    public boolean addToCart(Cart cart, Long productId, Long variantId, int quantity)
    {
        return cartRepository.addToCart(cart, productId, variantId, quantity);
    }

    public String getSessionId(HttpHeaders headers) {
        // Check cookies first
        Cookie sessionCookie = headers.getCookies().get(sessionKey);
        if (sessionCookie != null) {
            return sessionCookie.getValue();
        }

        // Generate a new session ID if not found
        return UUID.randomUUID().toString();
    }

    public String getSessionKey() {
        return sessionKey;
    }


    public boolean removeItemFromCart(Cart cart, Long itemId){
        return cartRepository.removeItemFromCart(cart, itemId);
    }

    public boolean updateCartQuantity(Cart cart, Long itemId, int quantity){
        CartItem cartItem = CartItem.findById(itemId);
        return cartRepository.updateCartQuantity(cart, cartItem, quantity);
    }


    public CartAddressRequestDTO updateCartAddress(Cart cart, CartAddressRequestDTO addressRequest) {
        return cartRepository.updateCartAddress(cart, addressRequest);
    }
}
