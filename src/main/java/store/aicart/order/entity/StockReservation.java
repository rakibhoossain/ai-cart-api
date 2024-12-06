package store.aicart.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import store.aicart.product.Product;
import store.aicart.product.ProductVariant;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_reservations")
public class StockReservation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant variant;

    @Column(name = "quantity", nullable = false)
    public int quantity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", nullable = false)
    public Cart cart;

    @Column(name = "expires_at", nullable = false)
    public Long expiresAt;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public static StockReservation findByProductAndCart(Long productId, Long cartId) {
        return find("product.id = ?1 and cartId = ?2", productId, cartId).firstResult();
    }

    public static StockReservation findByProductAndOrder(Long productId, Long orderId) {
        return find("product.id = ?1 and orderId = ?2", productId, orderId).firstResult();
    }

    public static void deleteExpiredReservations() {
        // Delete expired stock reservations
        delete("expiresAt < ?1", LocalDateTime.now());
    }
}
