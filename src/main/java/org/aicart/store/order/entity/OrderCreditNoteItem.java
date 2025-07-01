package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "order_credit_note_items")
public class OrderCreditNoteItem extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_note_id", nullable = false)
    public OrderCreditNote creditNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    public OrderItem orderItem;

    @Column(name = "quantity", nullable = false)
    public Integer quantity;

    @Column(name = "credit_amount", nullable = false)
    public BigInteger creditAmount;

    @Column(name = "reason")
    public String reason;
}
