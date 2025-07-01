package org.aicart.store.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.aicart.store.order.RefundTypeEnum;

import java.math.BigInteger;
import java.util.List;

public class OrderRefundRequestDTO {
    @NotNull
    private RefundTypeEnum refundType;
    
    @NotNull
    @Positive
    private BigInteger refundAmount;
    
    private String reason;
    private String adminNotes;
    private List<RefundItemRequestDTO> items;

    // Constructors
    public OrderRefundRequestDTO() {}

    // Getters and Setters
    public RefundTypeEnum getRefundType() { return refundType; }
    public void setRefundType(RefundTypeEnum refundType) { this.refundType = refundType; }

    public BigInteger getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigInteger refundAmount) { this.refundAmount = refundAmount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public List<RefundItemRequestDTO> getItems() { return items; }
    public void setItems(List<RefundItemRequestDTO> items) { this.items = items; }

    public static class RefundItemRequestDTO {
        @NotNull
        private Long orderItemId;
        
        @NotNull
        @Positive
        private Integer quantity;
        
        @NotNull
        @Positive
        private BigInteger refundAmount;
        
        private String reason;

        // Constructors
        public RefundItemRequestDTO() {}

        // Getters and Setters
        public Long getOrderItemId() { return orderItemId; }
        public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigInteger getRefundAmount() { return refundAmount; }
        public void setRefundAmount(BigInteger refundAmount) { this.refundAmount = refundAmount; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
