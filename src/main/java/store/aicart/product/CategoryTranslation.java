package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.country.Language;

@Entity(name = "category_translations")
public class CategoryTranslation extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    public Category category;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;

    @Column(length = 100, nullable = false)
    public String name;
}
