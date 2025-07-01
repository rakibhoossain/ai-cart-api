package org.aicart.store.order.dto;

import java.util.List;

public class OrderListResponse {
    private List<OrderListDTO> orders;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    // Constructors
    public OrderListResponse() {}

    public OrderListResponse(List<OrderListDTO> orders, long total, int page, int size) {
        this.orders = orders;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    // Getters and Setters
    public List<OrderListDTO> getOrders() { return orders; }
    public void setOrders(List<OrderListDTO> orders) { this.orders = orders; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
