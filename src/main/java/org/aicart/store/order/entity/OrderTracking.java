package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.OrderStatusEnum;
import org.aicart.store.order.TrackingEventTypeEnum;

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

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "event_type", nullable = false)
    public TrackingEventTypeEnum eventType = TrackingEventTypeEnum.STATUS_CHANGE;

    @Column(name = "title", nullable = false)
    public String title; // e.g., "Order Confirmed", "Package Shipped"

    @Column(name = "description", columnDefinition = "TEXT")
    public String description; // Detailed description

    @Column(name = "notes", columnDefinition = "TEXT")
    public String notes; // Internal admin notes

    @Column(name = "tracking_number")
    public String trackingNumber;

    @Column(name = "carrier")
    public String carrier;

    @Column(name = "carrier_service")
    public String carrierService; // e.g., "FedEx Express", "UPS Ground"

    @Column(name = "tracking_url")
    public String trackingUrl; // Direct link to carrier tracking

    @Column(name = "location")
    public String location; // Current package location

    @Column(name = "estimated_delivery")
    public LocalDateTime estimatedDelivery;

    @Column(name = "actual_delivery")
    public LocalDateTime actualDelivery;

    @Column(name = "is_public", nullable = false)
    public Boolean isPublic = true; // Whether customer can see this event

    @Column(name = "is_milestone", nullable = false)
    public Boolean isMilestone = false; // Important tracking milestone

    @Column(name = "created_by")
    public String createdBy; // Admin user who made the change

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "event_timestamp", nullable = false)
    public LocalDateTime eventTimestamp = LocalDateTime.now(); // When the actual event occurred

    // Tracking records are immutable for audit trail - no updates allowed
}
