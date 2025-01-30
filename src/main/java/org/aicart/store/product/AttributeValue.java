package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

/**
 * Stores values for each attribute, e.g., "Red" or "Medium."
 */
@Entity(name = "attribute_values")
public class AttributeValue extends PanacheEntity {

    @Column(length = 100, nullable = false)
    public String value;

    @ManyToOne(fetch = FetchType.LAZY)
    public Attribute attribute;

    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public List<AttributeValueTranslation> translations;
}
