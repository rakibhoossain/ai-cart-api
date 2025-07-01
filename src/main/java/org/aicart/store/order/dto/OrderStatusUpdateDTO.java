package org.aicart.store.order.dto;

import jakarta.validation.constraints.NotNull;
import org.aicart.store.order.OrderStatusEnum;

public class OrderStatusUpdateDTO {
    @NotNull
    private OrderStatusEnum status;
    
    private String notes;
    private String trackingNumber;
    private String carrier;
    private String estimatedDelivery; // ISO date string

    // Constructors
    public OrderStatusUpdateDTO() {}

    // Getters and Setters
    public OrderStatusEnum getStatus() { return status; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}
