package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.country.WarehouseLocation;

@Entity
@Table(
        name = "variant_stocks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"variant_id", "warehouse_id"})
)
public class VariantStock extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    @Column(nullable = false)
    public Integer quantity = 0;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    public WarehouseLocation warehouseLocation;
}
