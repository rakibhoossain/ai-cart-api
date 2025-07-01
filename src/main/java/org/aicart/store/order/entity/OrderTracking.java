package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.OrderStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
public class OrderTracking extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public OrderStatusEnum status;

    @Column(name = "notes", columnDefinition = "TEXT")
    public String notes;

    @Column(name = "tracking_number")
    public String trackingNumber;

    @Column(name = "carrier")
    public String carrier;

    @Column(name = "estimated_delivery")
    public LocalDateTime estimatedDelivery;

    @Column(name = "actual_delivery")
    public LocalDateTime actualDelivery;

    @Column(name = "created_by")
    public String createdBy; // Admin user who made the change

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
