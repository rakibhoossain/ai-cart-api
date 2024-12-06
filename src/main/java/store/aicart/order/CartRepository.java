package store.aicart.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import store.aicart.order.entity.Cart;
import store.aicart.order.entity.CartItem;
import store.aicart.product.Product;
import store.aicart.product.ProductVariant;
import store.aicart.user.entity.User;

@ApplicationScoped
public class CartRepository implements PanacheRepository<Cart> {

    @PersistenceContext
    EntityManager em;

    public Cart getCart(String sessionId, Long userId) {
        if (userId != null) {
            return find("user.id", userId).firstResult();  // Find Cart by userId
        } else {
            return find("sessionId", sessionId).firstResult();  // Find Cart by guestSessionId
        }
    }

    @Transactional
    public Cart createNewCart(String sessionId, User user) {
        // Create a new Cart and associate it with the session ID
        Cart newCart = new Cart();
        newCart.sessionId = sessionId;

        if(user != null) {
            newCart.user = user;
        }

        persist(newCart);
        return newCart;
    }


    @Transactional
    public boolean checkVariantStock(Long cartId, Long productId, Long variantId, int quantity) {
        String queryBuilder = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM product_variants pv
                                 JOIN products p ON pv.product_id = p.id
                                 LEFT JOIN variant_stocks vs ON pv.id = vs.variant_id
                                 LEFT JOIN (
                            SELECT variant_id, SUM(quantity) AS reserved_quantity
                            FROM stock_reservations
                            WHERE expires_at > EXTRACT(EPOCH FROM NOW())
                            AND cart_id <> :cartId
                            GROUP BY variant_id
                        ) sr ON pv.id = sr.variant_id
                        LEFT JOIN (
                                -- Sum of items in the current cart
                                SELECT variant_id, SUM(quantity) AS cart_quantity
                                FROM cart_items
                                WHERE cart_id = :cartId
                                GROUP BY variant_id
                            ) ci ON pv.id = ci.variant_id
                        WHERE p.id = :productId
                          AND pv.id = :variantId
                        GROUP BY pv.id
                        HAVING COALESCE(SUM(vs.quantity), 0) - COALESCE(SUM(sr.reserved_quantity), 0) - COALESCE(SUM(ci.cart_quantity), 0) >= :quantity
                    ) AS is_available;
                """;

        Boolean isAvailable = (Boolean) em.createNativeQuery(queryBuilder)
                .setParameter("cartId", cartId)
                .setParameter("productId", productId)
                .setParameter("variantId", variantId)
                .setParameter("quantity", quantity)
                .getSingleResult();

        return isAvailable != null && isAvailable;
    }


    @Transactional
    public CartItem findByCartProductAndVariant(Cart cart, Product product, ProductVariant variant) {
        return em.createQuery(
                        "SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.product = :product AND ci.variant = :variant",
                        CartItem.class)
                .setParameter("cart", cart)
                .setParameter("product", product)
                .setParameter("variant", variant)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }


    @Transactional
    public boolean addToCart(Cart cart, Long productId, Long variantId, int quantity)
    {
        boolean isStockAvailable = checkVariantStock(cart.id, productId, variantId, quantity);

        if (!isStockAvailable) {
            throw new IllegalArgumentException("Insufficient stock or invalid product/variant");
        }

        Product product = em.find(Product.class, productId);
        ProductVariant productVariant = em.find(ProductVariant.class, variantId);

        CartItem existingItem = findByCartProductAndVariant(cart, product, productVariant);
        if(existingItem != null) {
            existingItem.quantity += quantity;
            existingItem.persist();
            return true;
        }

        CartItem cartItem = new CartItem();
        cartItem.cart = cart;
        cartItem.product = product;
        cartItem.variant = productVariant;
        cartItem.quantity = quantity;
        cartItem.persist();

        return cartItem.id != null;
    }

}
