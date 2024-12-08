package store.aicart.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.PaymentTypeEnum;
import org.aicart.entity.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_payments")
public class OrderPayment extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "payment_type", nullable = false)
    public PaymentTypeEnum paymentType; // Stripe, PayPal, COD

    @Column(name = "transaction_id", nullable = true)
    public String transactionId; // Transaction from provider (Stripe/PayPal)

    @Column(name = "payment_status", nullable = false)
    public String paymentStatus; // E.g., "Paid", "Failed", "Pending", etc.

    @Column(name = "amount", nullable = false)
    public BigDecimal amount;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

