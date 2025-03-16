package org.aicart.store.product.entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.aicart.media.entity.FileStorageRelation;
import org.aicart.store.product.ProductStatusEnum;
import org.aicart.store.user.entity.Shop;
import org.aicart.util.StringSlugifier;

@Entity(name = "products")
public class Product extends PanacheEntity {

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public ProductStatusEnum status = ProductStatusEnum.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ProductTranslation> translations;

    @Column(length = 255, nullable = false)
    public String name;

    @Column(length = 255, unique = true, nullable = false)
    public String slug;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(name = "meta_title", length = 255)
    public String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    public String metaDescription;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    public Set<Category> categories;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_collection_pivot",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    public Set<ProductCollection> collections;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_tag_pivot",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    public Set<ProductTag> tags;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    public ProductShipping productShipping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    public ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_brand_id")
    public ProductBrand productBrand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ProductVariant> variants;

    @OneToMany(mappedBy = "associatedId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<FileStorageRelation> fileRelations;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ProductTaxRate> taxes; // Link to ProductTaxRate

//    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
//    public List<Discount> discounts;  // Global discounts

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

            while (Product.find("slug", uniqueSlug).firstResult() != null) {
                uniqueSlug = baseSlug + "-" + counter++;
            }

            this.slug = uniqueSlug;
        }
    }

}
