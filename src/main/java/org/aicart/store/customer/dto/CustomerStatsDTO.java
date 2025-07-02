package org.aicart.store.customer.dto;

public class CustomerStatsDTO {
    private long totalCustomers;
    private long verifiedCustomers;
    private long vipCustomers;
    private long lockedCustomers;
    private long newCustomersThisMonth;
    private long activeCustomers;
    private long inactiveCustomers;
    private long averageLifetimeValue;
    private double averageOrderValue;
    private long totalRevenue;

    // Constructors
    public CustomerStatsDTO() {}

    public CustomerStatsDTO(long totalCustomers, long verifiedCustomers, long vipCustomers, 
                           long lockedCustomers, long newCustomersThisMonth, long activeCustomers,
                           long inactiveCustomers, long averageLifetimeValue, 
                           double averageOrderValue, long totalRevenue) {
        this.totalCustomers = totalCustomers;
        this.verifiedCustomers = verifiedCustomers;
        this.vipCustomers = vipCustomers;
        this.lockedCustomers = lockedCustomers;
        this.newCustomersThisMonth = newCustomersThisMonth;
        this.activeCustomers = activeCustomers;
        this.inactiveCustomers = inactiveCustomers;
        this.averageLifetimeValue = averageLifetimeValue;
        this.averageOrderValue = averageOrderValue;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getVerifiedCustomers() { return verifiedCustomers; }
    public void setVerifiedCustomers(long verifiedCustomers) { this.verifiedCustomers = verifiedCustomers; }

    public long getVipCustomers() { return vipCustomers; }
    public void setVipCustomers(long vipCustomers) { this.vipCustomers = vipCustomers; }

    public long getLockedCustomers() { return lockedCustomers; }
    public void setLockedCustomers(long lockedCustomers) { this.lockedCustomers = lockedCustomers; }

    public long getNewCustomersThisMonth() { return newCustomersThisMonth; }
    public void setNewCustomersThisMonth(long newCustomersThisMonth) { this.newCustomersThisMonth = newCustomersThisMonth; }

    public long getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(long activeCustomers) { this.activeCustomers = activeCustomers; }

    public long getInactiveCustomers() { return inactiveCustomers; }
    public void setInactiveCustomers(long inactiveCustomers) { this.inactiveCustomers = inactiveCustomers; }

    public long getAverageLifetimeValue() { return averageLifetimeValue; }
    public void setAverageLifetimeValue(long averageLifetimeValue) { this.averageLifetimeValue = averageLifetimeValue; }

    public double getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }

    public long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(long totalRevenue) { this.totalRevenue = totalRevenue; }
}
