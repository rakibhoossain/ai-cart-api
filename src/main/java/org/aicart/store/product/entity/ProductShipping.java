package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity(name = "product_shippings")
public class ProductShipping extends PanacheEntityBase {

    @Id
    @Column(name = "product_id")
    public Long productId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "weight")
    public int weight = 0;

    @Column(name = "weight_unit", length = 6)
    public String weightUnit = "lb";
}
