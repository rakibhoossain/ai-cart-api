package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.ExchangeStatusEnum;


import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_exchanges")
public class OrderExchange extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Column(name = "exchange_number", unique = true, nullable = false)
    public String exchangeNumber;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public ExchangeStatusEnum status = ExchangeStatusEnum.PENDING;

    @Column(name = "reason", columnDefinition = "TEXT")
    public String reason;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    public String adminNotes;

    @Column(name = "price_difference")
    public BigInteger priceDifference = BigInteger.ZERO;

    @Column(name = "processed_by")
    public String processedBy; // Admin user who processed the exchange

    @Column(name = "processed_at")
    public LocalDateTime processedAt;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderExchangeItem> exchangeItems;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
