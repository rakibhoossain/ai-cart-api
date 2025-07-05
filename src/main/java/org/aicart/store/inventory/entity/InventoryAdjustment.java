package org.aicart.store.inventory.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.WarehouseLocation;
import org.aicart.store.product.entity.ProductVariant;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_adjustments")
public class InventoryAdjustment extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    public WarehouseLocation warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false)
    public AdjustmentType adjustmentType;

    @Column(nullable = false)
    public Integer quantity;

    @Column(name = "previous_quantity")
    public Integer previousQuantity;

    @Column(name = "new_quantity")
    public Integer newQuantity;

    @Column(nullable = false, length = 500)
    public String reason;

    @Column(columnDefinition = "TEXT")
    public String notes;

    @Column(name = "reference_number", length = 100)
    public String referenceNumber;

    @Column(name = "created_by", length = 100)
    public String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public enum AdjustmentType {
        INCREASE, DECREASE, SET, TRANSFER, SALE, RETURN, DAMAGE, RECOUNT
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
