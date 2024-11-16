package store.aicart.product;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "products")
public class Product extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String sku;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ProductTranslation> translations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    public Set<Category> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<ProductVariant> variants;

}
