package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity(name = "categories")
public class Category extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    public Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<Category> childCategories;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<CategoryTranslation> translations;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    public Set<Product> products;

}
