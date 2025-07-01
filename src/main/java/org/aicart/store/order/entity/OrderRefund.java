package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.RefundStatusEnum;
import org.aicart.store.order.RefundTypeEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_refunds")
public class OrderRefund extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Column(name = "refund_number", unique = true, nullable = false)
    public String refundNumber;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "refund_type", nullable = false)
    public RefundTypeEnum refundType; // FULL, PARTIAL

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public RefundStatusEnum status = RefundStatusEnum.PENDING;

    @Column(name = "refund_amount", nullable = false)
    public BigInteger refundAmount;

    @Column(name = "reason", columnDefinition = "TEXT")
    public String reason;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    public String adminNotes;

    @Column(name = "processed_by")
    public String processedBy; // Admin user who processed the refund

    @Column(name = "processed_at")
    public LocalDateTime processedAt;

    @OneToMany(mappedBy = "refund", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderRefundItem> refundItems;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
