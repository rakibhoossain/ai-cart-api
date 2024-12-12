package store.aicart.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import store.aicart.product.Product;
import store.aicart.product.ProductVariant;

import java.math.BigInteger;

@Entity(name = "discounts")
public class Discount extends PanacheEntity {

    @Column(name = "discount_type", nullable = false)
    public String discountType;  // 'PERCENTAGE' or 'FIXED'

    @Column(nullable = false)
    public BigInteger amount;  // Discount amount (in cents or percentage)

    @Column(name = "start_at", nullable = true)
    public BigInteger startAt;  // Unix timestamp for start date

    @Column(name = "end_at", nullable = true)
    public BigInteger endAt;    // Unix timestamp for end date

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    public Boolean isActive = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    public Product product;  // Associated product (if global discount)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    public ProductVariant variant;  // Associated variant (if variant-specific discount)
}
