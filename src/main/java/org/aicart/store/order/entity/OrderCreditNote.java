package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.CreditNoteStatusEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_credit_notes")
public class OrderCreditNote extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Column(name = "credit_note_number", unique = true, nullable = false)
    public String creditNoteNumber;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public CreditNoteStatusEnum status = CreditNoteStatusEnum.PENDING;

    @Column(name = "credit_amount", nullable = false)
    public BigInteger creditAmount;

    @Column(name = "reason", columnDefinition = "TEXT")
    public String reason;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    public String adminNotes;

    @Column(name = "expiry_date")
    public LocalDateTime expiryDate;

    @Column(name = "used_amount")
    public BigInteger usedAmount = BigInteger.ZERO;

    @Column(name = "remaining_amount")
    public BigInteger remainingAmount;

    @Column(name = "issued_by")
    public String issuedBy; // Admin user who issued the credit note

    @Column(name = "issued_at")
    public LocalDateTime issuedAt;

    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderCreditNoteItem> creditNoteItems;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.remainingAmount = this.creditAmount.subtract(this.usedAmount);
    }
}
