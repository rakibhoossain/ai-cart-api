package org.aicart.store.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.product.entity.Product;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"customer_id", "product_id"})
        })
public class Wishlist extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    public Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods for Panache queries
    public static Wishlist findByCustomerAndProduct(Customer customer, Product product) {
        return find("customer = ?1 and product = ?2", customer, product).firstResult();
    }

    public static long countByCustomer(Customer customer) {
        return count("customer = ?1", customer);
    }

    public static boolean existsByCustomerAndProduct(Customer customer, Product product) {
        return count("customer = ?1 and product = ?2", customer, product) > 0;
    }
}
