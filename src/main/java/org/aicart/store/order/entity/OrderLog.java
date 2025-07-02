package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.OrderLogTypeEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_logs")
public class OrderLog extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "log_type", nullable = false)
    public OrderLogTypeEnum logType;

    @Column(name = "title", nullable = false)
    public String title; // Short description of the action

    @Column(name = "description", columnDefinition = "TEXT")
    public String description; // Detailed description

    @Column(name = "old_value", columnDefinition = "TEXT")
    public String oldValue; // Previous value (for changes)

    @Column(name = "new_value", columnDefinition = "TEXT")
    public String newValue; // New value (for changes)

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata; // JSON metadata for additional context

    @Column(name = "ip_address")
    public String ipAddress; // IP address of the user who made the change

    @Column(name = "user_agent", columnDefinition = "TEXT")
    public String userAgent; // User agent of the browser/client

    @Column(name = "created_by")
    public String createdBy; // User who performed the action

    @Column(name = "created_by_type")
    public String createdByType; // ADMIN, CUSTOMER, SYSTEM

    @Column(name = "is_system_generated", nullable = false)
    public Boolean isSystemGenerated = false; // Whether this was auto-generated

    @Column(name = "is_visible_to_customer", nullable = false)
    public Boolean isVisibleToCustomer = true; // Whether customer can see this log

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    // Logs are immutable for audit trail
}
