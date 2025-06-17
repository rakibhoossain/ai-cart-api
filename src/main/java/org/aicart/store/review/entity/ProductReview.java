package org.aicart.store.review.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.product.entity.Product;
import org.aicart.store.customer.entity.Customer;

import java.time.LocalDateTime;

/**
 * Represents a review made on a product.  A review can be created by an authenticated customer
 * (linked via the {@link #customer} field) or by a guest user – in which case the reviewer must
 * supply a {@code name} and {@code email} address.
 */
@Entity(name = "product_reviews")
public class ProductReview extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    public Customer customer; // nullable – guest reviews have this null


    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "email", nullable = false)
    public String email;

    @Column(nullable = false)
    public int rating; // 1-5 stars

    @Column(name = "review_title", length = 255)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String body;

    @Column(name = "is_recommended")
    public Boolean recommended = Boolean.FALSE;

    @Column(name = "approved_at")
    public LocalDateTime approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
