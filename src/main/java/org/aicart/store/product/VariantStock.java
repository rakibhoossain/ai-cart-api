package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.WarehouseLocation;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    public WarehouseLocation warehouseLocation;
}
