package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.aicart.entity.Language;

@Entity(name = "category_translations")
@Table(
        name = "category_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "language_id"})
)
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
