package org.aicart.store.order.dto;

import java.math.BigInteger;

public class OrderRefundItemDTO {
    private Long id;
    private Long orderItemId;
    private String productName;
    private String variantName;
    private Integer quantity;
    private BigInteger refundAmount;
    private String reason;

    // Constructors
    public OrderRefundItemDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigInteger getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigInteger refundAmount) { this.refundAmount = refundAmount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
