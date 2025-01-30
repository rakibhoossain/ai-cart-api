package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.product.entity.Discount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity(name = "product_variants")
public class ProductVariant extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    public Product product;

    @Column(unique = true, nullable = false)
    public String sku;

    @Column(name = "image_id")
    public Long imageId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_variant_value",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    public Set<AttributeValue> attributeValues;

    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    public VariantStock stock;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<VariantPrice> prices;

    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    public List<Discount> discounts; // Variant-specific discounts
}
