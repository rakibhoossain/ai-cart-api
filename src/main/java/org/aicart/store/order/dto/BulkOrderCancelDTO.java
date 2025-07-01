package org.aicart.store.order.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BulkOrderCancelDTO {
    @NotEmpty
    private List<Long> orderIds;
    
    private String reason = "Bulk cancellation by admin";

    // Constructors
    public BulkOrderCancelDTO() {}

    public BulkOrderCancelDTO(List<Long> orderIds, String reason) {
        this.orderIds = orderIds;
        this.reason = reason;
    }

    // Getters and Setters
    public List<Long> getOrderIds() { return orderIds; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
