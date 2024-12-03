package store.aicart.product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.github.slugify.Slugify;
import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import store.aicart.product.entity.Category;

@Entity(name = "products")
public class Product extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String sku;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ProductTranslation> translations;

    @Column(length = 255, nullable = false)
    public String name;

    @Column(length = 255, unique = true, nullable = false)
    public String slug;

    @Column(columnDefinition = "TEXT")
    public String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    public Set<Category> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<ProductVariant> variants;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void generateUniqueSlugAndUpdateTimestamp() {
        updatedAt = LocalDateTime.now();

        if (this.slug == null || this.slug.isEmpty()) {
            final Slugify slg = Slugify.builder().build();
            final String baseSlug = slg.slugify(this.name);
            String uniqueSlug = baseSlug;
            int counter = 1;

            while (Product.find("slug", uniqueSlug).firstResult() != null) {
                uniqueSlug = baseSlug + "-" + counter++;
            }

            this.slug = uniqueSlug;
        }
    }

}
