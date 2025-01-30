package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;

@Entity(name = "attribute_translations")
@Table(
        name = "attribute_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"attribute_id", "language_id"})
)
public class AttributeTranslation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    public Attribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;

    @Column(length = 100, nullable = false)
    public String name;
}
