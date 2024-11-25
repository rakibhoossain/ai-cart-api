package store.aicart.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.aicart.country.Language;

@Entity(name = "category_translations")
public class CategoryTranslation extends PanacheEntityBase {

    @EmbeddedId
    public CategoryTranslationId id;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    public Category category;

    @MapsId("languageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;

    @Column(length = 100, nullable = false)
    public String name;
}
