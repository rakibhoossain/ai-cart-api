package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;

@Entity(name = "attribute_value_translations")
public class AttributeValueTranslation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", nullable = false)
    public AttributeValue attributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    public Language language;

    @Column(length = 100, nullable = false)
    public String value;
}
