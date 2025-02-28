package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.aicart.entity.Country;
import org.aicart.entity.Tax;
import org.aicart.store.product.entity.Product;

@Entity
@Table(name = "product_tax_rate")
public class ProductTaxRate extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    public Product product;  // The product

    @ManyToOne(fetch = FetchType.LAZY)
    public Tax tax;  // The associated tax rate

    @ManyToOne(fetch = FetchType.LAZY)
    public Country country;  // The country for this tax rule
}