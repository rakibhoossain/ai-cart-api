package org.aicart.store.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStatsDTO {
    
    // KPI Stats
    public BigDecimal totalRevenue;
    public BigDecimal revenueGrowthPercent;
    public Long totalCustomers;
    public BigDecimal customerGrowthPercent;
    public Long totalOrders;
    public BigDecimal orderGrowthPercent;
    public Long activeCustomers; // customers with orders in last 30 days
    public Long activeCustomersChange;
    
    // Chart Data
    public List<ChartDataPoint> revenueChart;
    public List<ChartDataPoint> orderChart;
    public List<CategoryDataPoint> topCategories;
    public List<RecentOrderDTO> recentOrders;
    
    // Constructors
    public DashboardStatsDTO() {}
    
    // Getters and Setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public BigDecimal getRevenueGrowthPercent() { return revenueGrowthPercent; }
    public void setRevenueGrowthPercent(BigDecimal revenueGrowthPercent) { this.revenueGrowthPercent = revenueGrowthPercent; }
    
    public Long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }
    
    public BigDecimal getCustomerGrowthPercent() { return customerGrowthPercent; }
    public void setCustomerGrowthPercent(BigDecimal customerGrowthPercent) { this.customerGrowthPercent = customerGrowthPercent; }
    
    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    
    public BigDecimal getOrderGrowthPercent() { return orderGrowthPercent; }
    public void setOrderGrowthPercent(BigDecimal orderGrowthPercent) { this.orderGrowthPercent = orderGrowthPercent; }
    
    public Long getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(Long activeCustomers) { this.activeCustomers = activeCustomers; }
    
    public Long getActiveCustomersChange() { return activeCustomersChange; }
    public void setActiveCustomersChange(Long activeCustomersChange) { this.activeCustomersChange = activeCustomersChange; }
    
    public List<ChartDataPoint> getRevenueChart() { return revenueChart; }
    public void setRevenueChart(List<ChartDataPoint> revenueChart) { this.revenueChart = revenueChart; }
    
    public List<ChartDataPoint> getOrderChart() { return orderChart; }
    public void setOrderChart(List<ChartDataPoint> orderChart) { this.orderChart = orderChart; }
    
    public List<CategoryDataPoint> getTopCategories() { return topCategories; }
    public void setTopCategories(List<CategoryDataPoint> topCategories) { this.topCategories = topCategories; }
    
    public List<RecentOrderDTO> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<RecentOrderDTO> recentOrders) { this.recentOrders = recentOrders; }
    
    // Inner classes for chart data
    public static class ChartDataPoint {
        public String date;
        public BigDecimal value;
        public Long count;
        
        public ChartDataPoint() {}
        
        public ChartDataPoint(String date, BigDecimal value, Long count) {
            this.date = date;
            this.value = value;
            this.count = count;
        }
        
        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
    
    public static class CategoryDataPoint {
        public String category;
        public BigDecimal revenue;
        public Long orderCount;
        public String color;
        
        public CategoryDataPoint() {}
        
        public CategoryDataPoint(String category, BigDecimal revenue, Long orderCount, String color) {
            this.category = category;
            this.revenue = revenue;
            this.orderCount = orderCount;
            this.color = color;
        }
        
        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        
        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    public static class RecentOrderDTO {
        public Long orderId;
        public String customerName;
        public String customerEmail;
        public BigDecimal orderTotal;
        public String orderDate;
        public String status;
        
        public RecentOrderDTO() {}
        
        // Getters and Setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        
        public BigDecimal getOrderTotal() { return orderTotal; }
        public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }
        
        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
