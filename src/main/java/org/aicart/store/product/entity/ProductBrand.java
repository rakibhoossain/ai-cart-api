package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "product_brands")
public class ProductBrand extends PanacheEntity {

    @Column(length = 255, nullable = false)
    public String name;
}
