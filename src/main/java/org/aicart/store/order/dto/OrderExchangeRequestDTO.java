package org.aicart.store.order.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderExchangeRequestDTO {
    private String reason;
    private String adminNotes;
    private List<ExchangeItemRequestDTO> items;

    // Constructors
    public OrderExchangeRequestDTO() {}

    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public List<ExchangeItemRequestDTO> getItems() { return items; }
    public void setItems(List<ExchangeItemRequestDTO> items) { this.items = items; }

    public static class ExchangeItemRequestDTO {
        @NotNull
        private Long originalOrderItemId;
        
        @NotNull
        private Long newVariantId;
        
        @NotNull
        private Integer quantity;
        
        private String reason;

        // Constructors
        public ExchangeItemRequestDTO() {}

        // Getters and Setters
        public Long getOriginalOrderItemId() { return originalOrderItemId; }
        public void setOriginalOrderItemId(Long originalOrderItemId) { this.originalOrderItemId = originalOrderItemId; }

        public Long getNewVariantId() { return newVariantId; }
        public void setNewVariantId(Long newVariantId) { this.newVariantId = newVariantId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
