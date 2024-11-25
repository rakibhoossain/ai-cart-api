package store.aicart.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_categories")
public class ProductCategory extends PanacheEntity {

    @Column(name = "name", nullable = false, length = 100)
    public String name;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    public ProductCategory parentCategory;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to fetch child categories
    public List<ProductCategory> getChildren() {
        return find("parentCategory", this).list();
    }
}
