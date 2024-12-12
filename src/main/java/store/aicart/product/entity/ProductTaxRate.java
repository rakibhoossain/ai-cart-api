package store.aicart.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Country;
import org.aicart.entity.Tax;
import store.aicart.product.Product;

@Entity(name = "product_tax")
@Table(
        name = "product_tax",
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
