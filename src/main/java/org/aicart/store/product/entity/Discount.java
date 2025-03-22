package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.product.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity(name = "discounts")
public class Discount extends PanacheEntity {

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "discount_type", nullable = false)
    public ProductDiscountEnum discountType;  // moneyOffProduct, buyXgetY, moneyOffOrder, shipping

    @Column(name = "is_automatic", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    public Boolean isAutomatic = Boolean.TRUE;

    @Column(nullable = true, unique = true)
    public String coupon;

    @Column(nullable = false)
    public String title;

    @Column(name = "start_at", nullable = false)
    public BigInteger startAt; // Unix timestamp stored as Instant

    @Column(name = "end_at", nullable = true)
    public BigInteger endAt; // Unix timestamp for end date (Optional)

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, name = "amount_type")
    public ProductDiscountAmountTypEnum amountType;

    @Column(nullable = false)
    public BigInteger amount;  // Discount amount (in cents or percentage)

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    public Boolean isActive = Boolean.FALSE;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public DiscountPurchaseType purchaseType;

    @Column(name = "applies_to", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public DiscountAppliesToEnum appliesTo = DiscountAppliesToEnum.PRODUCT; // Default value

    @Column(name = "min_amount")
    public Integer minAmount; // Nullable

    @Column(name = "min_quantity")
    public Integer minQuantity; // Nullable

    @ElementCollection
    public List<CombinationEnum> combinations;

    @ElementCollection
    @CollectionTable(name = "discount_locations", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "location_id")
    public List<Long> locations; // Stores location IDs

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "discount_collection_pivot",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    public Set<ProductCollection> collections;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "discount_variant_pivot",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "variant_id")
    )
    public Set<ProductVariant> variants;

    @Column(name = "max_use")
    public Integer maxUse;

    @Column(name = "max_customer_use")
    public Integer maxCustomerUse;

    @Column(name = "used_count", nullable = true)
    public Integer usedCount = 0;   // Used count

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }
}
