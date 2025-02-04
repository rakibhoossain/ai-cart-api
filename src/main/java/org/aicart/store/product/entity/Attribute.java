package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

/**
 * Defines an attribute type like Size or Color
 */
@Entity(name = "attributes")
public class Attribute extends PanacheEntity {

    @Column(length = 100, nullable = false)
    public String name;
    
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<AttributeTranslation> translations;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<AttributeValue> values;
}
