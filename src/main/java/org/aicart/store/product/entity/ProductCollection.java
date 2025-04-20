package org.aicart.store.product.entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.media.entity.FileStorage;
import org.aicart.store.product.ProductCollectionTypeEnum;
import org.aicart.store.product.ProductConditionMatchEnum;
import org.aicart.store.user.entity.Shop;
import org.aicart.util.StringSlugifier;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_collections")
public class ProductCollection extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "collection_type", nullable = false)
    public ProductCollectionTypeEnum collectionType; // smart, manual

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "condition_match")
    public ProductConditionMatchEnum conditionMatch;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_collection_pivot",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    public Set<Product> products = new HashSet<>();

    @OneToMany(
            mappedBy = "collection",
            cascade = CascadeType.ALL // Ensures operations cascade
    )
    public Set<ProductCollectionCondition> conditions;

    @Column(name = "is_active")
    public Boolean isActive;

    @ElementCollection
    @CollectionTable(name = "collection_location_pivot", joinColumns = @JoinColumn(name = "collection_id"))
    @Column(name = "location_id")
    public List<Long> locations; // Stores location IDs

    @Column(name = "name", nullable = false)
    public String name;

    @Column(length = 255, unique = true, nullable = false)
    public String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    public FileStorage file;

    @Column(name = "meta_title", length = 255)
    public String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    public String metaDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void generateUniqueSlugAndUpdateTimestamp() {
        updatedAt = LocalDateTime.now();

        if (this.slug == null || this.slug.isEmpty()) {
            final String baseSlug = StringSlugifier.slugify(this.name);
            String uniqueSlug = baseSlug;
            int counter = 1;

            while (ProductCollection.find("slug", uniqueSlug).firstResult() != null) {
                uniqueSlug = baseSlug + "-" + counter++;
            }

            this.slug = uniqueSlug;
        }
    }
}
