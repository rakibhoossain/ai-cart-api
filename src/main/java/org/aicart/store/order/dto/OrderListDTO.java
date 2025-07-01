package org.aicart.store.order.dto;

import org.aicart.PaymentStatusEnum;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.order.OrderStatusEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class OrderListDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private BigInteger totalPrice;
    private String currency;
    private OrderStatusEnum status;
    private PaymentStatusEnum paymentStatus;
    private PaymentTypeEnum paymentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer itemCount;

    // Constructors
    public OrderListDTO() {}

    public OrderListDTO(Long id, String customerName, String customerEmail, BigInteger totalPrice, 
                       String currency, OrderStatusEnum status, PaymentStatusEnum paymentStatus, 
                       PaymentTypeEnum paymentType, LocalDateTime createdAt, LocalDateTime updatedAt, 
                       Integer itemCount) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentType = paymentType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.itemCount = itemCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigInteger getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigInteger totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public OrderStatusEnum getStatus() { return status; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }

    public PaymentStatusEnum getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatusEnum paymentStatus) { this.paymentStatus = paymentStatus; }

    public PaymentTypeEnum getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentTypeEnum paymentType) { this.paymentType = paymentType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
}
