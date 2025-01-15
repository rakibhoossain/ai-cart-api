package store.aicart.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import store.aicart.order.dto.CartAddressRequestDTO;
import store.aicart.order.dto.CartItemDTO;
import store.aicart.order.dto.CartResponseDTO;
import store.aicart.order.entity.Cart;
import store.aicart.order.entity.CartDeliveryRequestDTO;
import store.aicart.order.entity.CartItem;
import store.aicart.user.entity.User;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CartService {

    private final String sessionKey = "cart-session";

    @Inject
    CartRepository cartRepository;

    public Cart getCartEntity(String sessionId) {
        return cartRepository.getCart(sessionId);
    }

    public CartResponseDTO getCart(String sessionId) {
        Cart cart = cartRepository.getCart(sessionId);

        if(cart != null) {
            List<CartItemDTO> cartItems = cartRepository.getCartItems(cart);
            return new CartResponseDTO(cart, cartItems);
        }

        return null;
    }

    @Transactional
    public Cart firstOrCreate(String sessionId, Long userId) {
        Cart cart = cartRepository.getCart(sessionId);

        if(cart == null) {
            User user = userId != null ? User.find("id", userId).firstResult() : null;
            return cartRepository.createNewCart(sessionId, user);
        }

        if(cart.user == null) {
            User user = userId != null ? User.find("id", userId).firstResult() : null;
            if(user != null) {
                cart.user = user;
                cart.persist();
            }
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


    public CartDeliveryRequestDTO updateDeliveryInfo(Cart cart, CartDeliveryRequestDTO deliveryRequest) {
        return cartRepository.updateDeliveryInfo(cart, deliveryRequest);
    }
}
