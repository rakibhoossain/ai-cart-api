package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "product_types")
public class ProductType extends PanacheEntity {

    @Column(length = 255, nullable = false)
    public String name;
}
