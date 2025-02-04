package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;

@Entity(name = "product_translations")
public class ProductTranslation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product; // This is the owning side of the relationship

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;

    @Column(length = 255, nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
