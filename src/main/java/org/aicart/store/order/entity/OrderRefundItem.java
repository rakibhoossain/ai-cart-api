package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "order_refund_items")
public class OrderRefundItem extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id", nullable = false)
    public OrderRefund refund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    public OrderItem orderItem;

    @Column(name = "quantity", nullable = false)
    public Integer quantity;

    @Column(name = "refund_amount", nullable = false)
    public BigInteger refundAmount;

    @Column(name = "reason")
    public String reason;
}
