package org.aicart.store.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.order.dto.CartAddressRequestDTO;
import org.aicart.store.order.dto.CartItemDTO;
import org.aicart.store.order.dto.CartResponseDTO;
import org.aicart.store.order.entity.Cart;
import org.aicart.store.order.entity.CartDeliveryRequestDTO;
import org.aicart.store.order.entity.CartItem;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CartService {

    private final String sessionKey = "cart-session";

    @Inject
    CartRepository cartRepository;

    @Inject
    ShopContext shopContext;

    public Cart getCartEntity(String sessionId) {
        return cartRepository.getCart(sessionId, shopContext.getShopId());
    }

    public CartResponseDTO getCart(String sessionId) {
        Cart cart = cartRepository.getCart(sessionId, shopContext.getShopId());

        if(cart != null) {
            List<CartItemDTO> cartItems = cartRepository.getCartItems(cart);
            return new CartResponseDTO(cart, cartItems);
        }

        return null;
    }

    @Transactional
    public Cart firstOrCreate(String sessionId, Long customerId) {
        Cart cart = cartRepository.getCart(sessionId, shopContext.getShopId());

        if(cart == null) {
            Customer customer = customerId != null ? Customer.find("id = ?1 AND shop.id = ?2", customerId, shopContext.getShopId()).firstResult() : null;
            return cartRepository.createNewCart(sessionId, customer, shopContext.getShopId());
        }

        if(cart.customer == null) {
            Customer customer = customerId != null ? Customer.find("id = ?1 AND shop.id = ?2", customerId, shopContext.getShopId()).firstResult() : null;
            if(customer != null) {
                cart.customer = customer;
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
