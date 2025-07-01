package org.aicart.store.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BulkOrderStatusUpdateDTO {
    @NotEmpty
    private List<Long> orderIds;
    
    @NotNull
    @Valid
    private OrderStatusUpdateDTO statusUpdate;

    // Constructors
    public BulkOrderStatusUpdateDTO() {}

    public BulkOrderStatusUpdateDTO(List<Long> orderIds, OrderStatusUpdateDTO statusUpdate) {
        this.orderIds = orderIds;
        this.statusUpdate = statusUpdate;
    }

    // Getters and Setters
    public List<Long> getOrderIds() { return orderIds; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }

    public OrderStatusUpdateDTO getStatusUpdate() { return statusUpdate; }
    public void setStatusUpdate(OrderStatusUpdateDTO statusUpdate) { this.statusUpdate = statusUpdate; }
}
