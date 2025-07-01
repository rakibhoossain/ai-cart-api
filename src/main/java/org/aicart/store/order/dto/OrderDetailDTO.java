package org.aicart.store.order.dto;

import org.aicart.PaymentStatusEnum;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.order.OrderStatusEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailDTO {
    private Long id;
    private String sessionId;
    private CustomerInfoDTO customer;
    private BigInteger totalPrice;
    private BigInteger subTotal;
    private BigInteger totalDiscount;
    private BigInteger shippingCost;
    private BigInteger totalTax;
    private String currency;
    private OrderStatusEnum status;
    private PaymentStatusEnum paymentStatus;
    private PaymentTypeEnum paymentType;
    private OrderBillingDTO billing;
    private OrderShippingDTO shipping;
    private List<OrderItemDetailDTO> items;
    private List<OrderTrackingDTO> tracking;
    private List<OrderRefundDTO> refunds;
    private List<OrderExchangeDTO> exchanges;
    private List<OrderCreditNoteDTO> creditNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public OrderDetailDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public CustomerInfoDTO getCustomer() { return customer; }
    public void setCustomer(CustomerInfoDTO customer) { this.customer = customer; }

    public BigInteger getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigInteger totalPrice) { this.totalPrice = totalPrice; }

    public BigInteger getSubTotal() { return subTotal; }
    public void setSubTotal(BigInteger subTotal) { this.subTotal = subTotal; }

    public BigInteger getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigInteger totalDiscount) { this.totalDiscount = totalDiscount; }

    public BigInteger getShippingCost() { return shippingCost; }
    public void setShippingCost(BigInteger shippingCost) { this.shippingCost = shippingCost; }

    public BigInteger getTotalTax() { return totalTax; }
    public void setTotalTax(BigInteger totalTax) { this.totalTax = totalTax; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public OrderStatusEnum getStatus() { return status; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }

    public PaymentStatusEnum getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatusEnum paymentStatus) { this.paymentStatus = paymentStatus; }

    public PaymentTypeEnum getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentTypeEnum paymentType) { this.paymentType = paymentType; }

    public OrderBillingDTO getBilling() { return billing; }
    public void setBilling(OrderBillingDTO billing) { this.billing = billing; }

    public OrderShippingDTO getShipping() { return shipping; }
    public void setShipping(OrderShippingDTO shipping) { this.shipping = shipping; }

    public List<OrderItemDetailDTO> getItems() { return items; }
    public void setItems(List<OrderItemDetailDTO> items) { this.items = items; }

    public List<OrderTrackingDTO> getTracking() { return tracking; }
    public void setTracking(List<OrderTrackingDTO> tracking) { this.tracking = tracking; }

    public List<OrderRefundDTO> getRefunds() { return refunds; }
    public void setRefunds(List<OrderRefundDTO> refunds) { this.refunds = refunds; }

    public List<OrderExchangeDTO> getExchanges() { return exchanges; }
    public void setExchanges(List<OrderExchangeDTO> exchanges) { this.exchanges = exchanges; }

    public List<OrderCreditNoteDTO> getCreditNotes() { return creditNotes; }
    public void setCreditNotes(List<OrderCreditNoteDTO> creditNotes) { this.creditNotes = creditNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
