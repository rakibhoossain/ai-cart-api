package org.aicart.store.order.dto;

import java.math.BigInteger;

public class OrderStatsDTO {
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigInteger totalRevenue;

    // Constructors
    public OrderStatsDTO() {}

    public OrderStatsDTO(long totalOrders, long pendingOrders, long completedOrders, 
                        long cancelledOrders, BigInteger totalRevenue) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

    public long getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }

    public long getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }

    public BigInteger getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigInteger totalRevenue) { this.totalRevenue = totalRevenue; }
}
