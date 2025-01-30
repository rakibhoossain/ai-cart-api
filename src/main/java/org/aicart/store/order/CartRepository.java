package org.aicart.store.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.aicart.store.order.dto.CartAddressRequestDTO;
import org.aicart.store.order.dto.CartItemDTO;
import org.aicart.store.order.entity.Cart;
import org.aicart.store.order.entity.CartDeliveryRequestDTO;
import org.aicart.store.order.entity.CartItem;
import org.aicart.store.order.entity.StockReservation;
import org.aicart.store.product.Product;
import org.aicart.store.product.ProductVariant;
import org.aicart.store.user.entity.User;

import java.util.List;

@ApplicationScoped
public class CartRepository implements PanacheRepository<Cart> {

    @PersistenceContext
    EntityManager em;

    public Cart getCart(String sessionId) {
        return find("sessionId", sessionId).firstResult();  // Find Cart by guestSessionId
    }

    public Cart createNewCart(String sessionId, User user) {
        // Create a new Cart and associate it with the session ID
        Cart newCart = new Cart();
        newCart.sessionId = sessionId;
        newCart.step = 0;

        if(user != null) {
            newCart.user = user;
        }

        persist(newCart);
        return newCart;
    }


    @Transactional
    public boolean checkVariantStock(Long cartId, Long productId, Long variantId, int quantity, boolean isReplace) {
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
                        HAVING COALESCE(SUM(vs.quantity), 0)
                                - COALESCE(SUM(sr.reserved_quantity), 0)
                                - CASE WHEN :isReplace THEN 0 ELSE COALESCE(SUM(ci.cart_quantity), 0) END
                                >= :quantity
                    ) AS is_available;
                """;

        Boolean isAvailable = (Boolean) em.createNativeQuery(queryBuilder)
                .setParameter("cartId", cartId)
                .setParameter("productId", productId)
                .setParameter("variantId", variantId)
                .setParameter("quantity", quantity)
                .setParameter("isReplace", isReplace)
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
        boolean isStockAvailable = checkVariantStock(cart.id, productId, variantId, quantity, false);

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


    @Transactional
    public List<CartItemDTO> getCartItems(Cart cart) {

        int languageId = 1; // Static language ID for now
        int countryId = 1; // Static country ID for now
        long currentTimestamp = System.currentTimeMillis() / 1000L; // Static Unix timestamp for now

        String query = """
            SELECT
                cit.id AS id,
                p.id AS product_id,
                cit.variant_id AS variant_id,
                COALESCE(locale.name, p.name) AS product_name,
                p.slug AS slug,
                locale.id AS locale_id,
                locale.name AS locale_name,
                p.sku AS sku,
                cit.quantity AS quantity,
                (
                    SELECT jsonb_agg(
                                   jsonb_build_object(
                                           'id', fs.id,
                                           'relation_id', fsr.id,
                                           'original_url', fs.original_url,
                                           'medium_url', fs.medium_url,
                                           'storage_location', fs.storage_location,
                                           'score', fsr.score
                                   )
                           )
                    FROM file_storage_relation fsr
                             JOIN file_storage fs
                                  ON fsr.file_id = fs.id
                    WHERE fsr.associated_id = p.id AND fsr.associated_type = 1
                ) AS images,
                (
                    SELECT jsonb_agg(
                                   jsonb_build_object(
                                           'id', c.id,
                                           'name', c.name,
                                           'category_id', pc.category_id,
                                           'depth', cc.depth
                                   )
                           )
                    FROM product_category pc
                             JOIN category_closure cc
                                  ON pc.category_id = cc.descendant_id
                             JOIN categories c
                                  ON cc.ancestor_id = c.id
                    WHERE pc.product_id = p.id
                ) AS categories,
                (
                    SELECT jsonb_build_object(
                                   'id', pv.id,
                                   'sku', pv.sku,
                                   'stock', (
                                       SELECT SUM(vs.quantity)
                                       FROM variant_stocks vs
                                       WHERE vs.variant_id = pv.id
                                   ),
                                   'price', (
                                       SELECT jsonb_build_object(
                                                      'currency_id', vp.currency_id,
                                                      'price', vp.price,
                                                      'discount', COALESCE(d.amount, 0),
                                                      'discount_end_at', d.end_at,
                                                      'discount_type', d.discount_type,
                                                      'tax_rate', COALESCE(t.tax_rate, 0)
                                              )
                                       FROM variant_prices vp
                                       LEFT JOIN discounts d
                                            ON (d.variant_id = pv.id OR (d.variant_id IS NULL AND d.product_id = p.id))
                                            AND d.is_active = true
                                            AND (d.start_at IS NULL OR d.start_at <= :currentTimestamp)
                                            AND (d.end_at IS NULL OR d.end_at >= :currentTimestamp)
                                       LEFT JOIN product_tax pt ON pt.product_id = p.id
                                            AND pt.country_id = :countryId
                                            LEFT JOIN taxes t
                                            ON pt.tax_id = t.id
                                       WHERE vp.variant_id = pv.id AND vp.country_id = :countryId
                                   ),
                                    'image_id', pv.image_id,
                                   'attributes', (
                                       SELECT jsonb_agg(
                                                      jsonb_build_object(
                                                              'attribute_name', a.name,
                                                              'value', av.value,
                                                              'attribute_id', av.attribute_id,
                                                              'value_id', av.id
                                                      )
                                                  )
                                       FROM product_variant_value pvv
                                                JOIN attribute_values av
                                                     ON pvv.attribute_value_id = av.id
                                                JOIN attributes a
                                                     ON av.attribute_id = a.id
                                       WHERE pvv.variant_id = pv.id
                                   )
                           )
                    FROM product_variants pv
                    WHERE pv.product_id = p.id
                    AND pv.id = cit.variant_id
                ) AS variant
            FROM products p
                JOIN cart_items cit ON p.id = cit.product_id
                     LEFT JOIN product_translations locale
                               ON p.id = locale.product_id AND locale.language_id = :languageId
            WHERE cit.cart_id = :cartId
            ORDER BY cit.id DESC
            """;

        return em.createNativeQuery(query, CartItemDTO.class)
                .setParameter("languageId", languageId)
                .setParameter("countryId", countryId)
                .setParameter("cartId", cart.id)
                .setParameter("currentTimestamp", currentTimestamp)
                .getResultList();
    }

    @Transactional
    public boolean removeItemFromCart(Cart cart, Long itemId){
        CartItem cartItem = CartItem.find("id = ?1 and cart.id = ?2", itemId, cart.id).firstResult();
        if (cartItem != null) {
            StockReservation.delete(
                    "cart.id = :cartId AND product.id = :productId AND variant.id = :variantId",
                    Parameters.with("cartId", cart.id)
                            .and("productId", cartItem.product.id)
                            .and("variantId", cartItem.variant.id)
            );
            cartItem.delete();

            // Check if the cart is now empty
            long remainingItems = CartItem.count("cart.id = ?1", cart.id);
            if (remainingItems == 0) {
                // Remove the cart if no items are left
                cart.delete();
            }

            return true;
        }

        return false;
    }

    @Transactional
    public boolean removeItemFromCart(Cart cart){

//        if(cart == null) return false;

        CartItem.delete("cart.id = :cartId",
                Parameters.with("cartId", cart.id)
        );

        StockReservation.delete(
                "cart.id = :cartId",
                Parameters.with("cartId", cart.id)
        );

        Cart.delete("id = :cartId", Parameters.with("cartId", cart.id));

//        cart.delete();

        return true;
    }


    @Transactional
    public boolean updateCartQuantity(Cart cart, CartItem cartItem, int quantity) {
        // Check stock availability before updating the cart
        boolean isStockAvailable = checkVariantStock(cart.id, cartItem.product.id, cartItem.variant.id, quantity, true);

        if (!isStockAvailable) {
            throw new IllegalArgumentException("Insufficient stock or invalid product/variant");
        }

        // Raw SQL query to update cart item quantity
        String updateQuery = """
        UPDATE cart_items
        SET quantity = :quantity, updated_at = NOW()
        WHERE id = :cartItemId AND cart_id = :cartId
        """;

        int updatedRows = em.createNativeQuery(updateQuery)
                .setParameter("quantity", quantity)
                .setParameter("cartItemId", cartItem.id)
                .setParameter("cartId", cart.id)
                .executeUpdate();

        return updatedRows > 0;
    }



    @Transactional
    public void reserveStock(Long productId, Long variantId, int quantity) {
        long expiresAt = System.currentTimeMillis() / 1000 + 15 * 60; // Unix time + 15 minutes

        em.createNativeQuery("""
        INSERT INTO stock_reservations (product_id, variant_id, quantity, expires_at)
        VALUES (:productId, :variantId, :quantity, :expiresAt)
    """)
                .setParameter("productId", productId)
                .setParameter("variantId", variantId)
                .setParameter("quantity", quantity)
                .setParameter("expiresAt", expiresAt)
                .executeUpdate();
    }


    @Transactional
    public void cleanupExpiredReservations() {
        long currentTime = System.currentTimeMillis() / 1000;

        em.createNativeQuery("DELETE FROM stock_reservations WHERE expires_at <= :currentTime")
                .setParameter("currentTime", currentTime)
                .executeUpdate();
    }

    @Transactional
    public CartAddressRequestDTO updateCartAddress(Cart cart, CartAddressRequestDTO addressRequest) {

        cart.step = 1;
        cart.billing = addressRequest.getBilling();
        if(!addressRequest.isUseShippingAsBilling()) {
            cart.shipping = addressRequest.getShipping();
        } else {
            cart.shipping = null;
        }

        em.merge(cart);

        return addressRequest;
    }

    @Transactional
    public CartDeliveryRequestDTO updateDeliveryInfo(Cart cart, CartDeliveryRequestDTO deliveryRequest) {

        cart.step = 2;
        cart.deliveryMethod = deliveryRequest.getDeliveryMethod();
        cart.paymentMethod = deliveryRequest.getPaymentMethod();
        cart.couponCode = deliveryRequest.getCouponCode();

        em.merge(cart);

        return deliveryRequest;
    }

}
