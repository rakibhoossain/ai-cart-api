package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.Shop;

import java.util.List;

/**
 * Defines an attribute type like Size or Color
 */
@Entity(name = "attributes")
public class Attribute extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Column(length = 100, nullable = false)
    public String name;
    
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<AttributeTranslation> translations;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<AttributeValue> values;
}
