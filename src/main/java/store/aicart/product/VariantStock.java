package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity(name = "variant_stocks")
public class VariantStock extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    @Column(nullable = false)
    public Integer quantity;

    public String warehouseLocation;

}
