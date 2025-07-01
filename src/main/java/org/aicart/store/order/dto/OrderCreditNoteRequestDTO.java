package org.aicart.store.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;
import java.util.List;

public class OrderCreditNoteRequestDTO {
    @NotNull
    @Positive
    private BigInteger creditAmount;
    
    private String reason;
    private String adminNotes;
    private String expiryDate; // ISO date string
    private List<CreditNoteItemRequestDTO> items;

    // Constructors
    public OrderCreditNoteRequestDTO() {}

    // Getters and Setters
    public BigInteger getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigInteger creditAmount) { this.creditAmount = creditAmount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public List<CreditNoteItemRequestDTO> getItems() { return items; }
    public void setItems(List<CreditNoteItemRequestDTO> items) { this.items = items; }

    public static class CreditNoteItemRequestDTO {
        @NotNull
        private Long orderItemId;
        
        @NotNull
        @Positive
        private Integer quantity;
        
        @NotNull
        @Positive
        private BigInteger creditAmount;
        
        private String reason;

        // Constructors
        public CreditNoteItemRequestDTO() {}

        // Getters and Setters
        public Long getOrderItemId() { return orderItemId; }
        public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigInteger getCreditAmount() { return creditAmount; }
        public void setCreditAmount(BigInteger creditAmount) { this.creditAmount = creditAmount; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
