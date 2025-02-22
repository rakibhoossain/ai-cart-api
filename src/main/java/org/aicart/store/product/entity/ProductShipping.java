package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity(name = "product_shippings")
public class ProductShipping extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    public Product product;

    @Column(name = "weight")
    public int weight = 0;

    @Column(name = "weight_unit", length = 6)
    public String weightUnit = "lb";
}
