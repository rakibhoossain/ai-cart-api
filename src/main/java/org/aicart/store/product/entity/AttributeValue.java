package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

/**
 * Stores values for each attribute, e.g., "Red" or "Medium."
 */
@Entity(name = "attribute_values")
public class AttributeValue extends PanacheEntity {

    @Column(length = 50, nullable = false)
    public String value;

    @Column(length = 20)
    public String color;

    @Column(name = "image_id")
    public Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    public Attribute attribute;

    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public List<AttributeValueTranslation> translations;
}
