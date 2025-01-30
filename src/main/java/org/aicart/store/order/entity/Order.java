package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.order.OrderStatusEnum;
import org.aicart.PaymentStatusEnum;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.user.entity.User;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    public User user;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "currency_id", nullable = false)
//    public Currency currency;

    @Column(name = "currency", nullable = false)
    public String currency; // ISO 3166-1 alpha-2

    @Column(name = "session_id", nullable = true)
    public String sessionId; // UUID for guest users

    @Column(name = "total_price", nullable = false)
    public BigInteger totalPrice;

    @Column(name = "sub_total", nullable = false)
    public BigInteger subTotal;

    @Column(name = "total_discount", nullable = false)
    public BigInteger totalDiscount = BigInteger.ZERO;

    @Column(name = "shipping_cost", nullable = false)
    public BigInteger shippingCost = BigInteger.ZERO;

    @Column(name = "total_tax", nullable = false)
    public BigInteger totalTax = BigInteger.ZERO;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public OrderStatusEnum status = OrderStatusEnum.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderItem> items;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public OrderBilling billing;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public OrderShipping shipping;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "billing_id", nullable = false)
//    public OrderBilling billing;
//
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "shipping_id", nullable = true)
//    public OrderShipping shipping;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true)
    public OrderPayment payment;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "payment_type", nullable = false)
    public PaymentTypeEnum paymentType; // Enum to track payment type, COD, Stripe

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "payment_status", nullable = false)
    public PaymentStatusEnum paymentStatus; // Enum to track payment type

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
