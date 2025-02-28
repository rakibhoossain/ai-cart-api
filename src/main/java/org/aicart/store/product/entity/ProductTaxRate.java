package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Country;
import org.aicart.entity.Tax;

@Entity(name = "product_tax_rate")
@Table(
        name = "product_tax_rate",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "country_id"})
)
public class ProductTaxRate extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;  // The product

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id", nullable = false)
    public Tax tax;  // The associated tax rate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    public Country country;  // The country for this tax rule
}
