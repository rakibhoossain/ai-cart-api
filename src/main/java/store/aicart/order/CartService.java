package store.aicart.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import store.aicart.order.dto.CartItemDTO;
import store.aicart.order.entity.Cart;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CartService {

    private final String sessionKey = "cart-session";

    @Inject
    CartRepository cartRepository;

    public List<CartItemDTO> getCart(String sessionId, Long userId) {
        Cart cart = cartRepository.getCart(sessionId, userId);

        if(cart != null) {
            return cartRepository.getCartItems(cart);
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
}
