package org.aicart.store.customer.dto;

import java.util.List;

public class CustomerListResponseDTO {
    private List<CustomerListDTO> customers;
    private long total;
    private int page;
    private int size;
    private int totalPages;
    private CustomerStatsDTO stats;

    // Constructors
    public CustomerListResponseDTO() {}

    public CustomerListResponseDTO(List<CustomerListDTO> customers, long total, int page, int size) {
        this.customers = customers;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    public CustomerListResponseDTO(List<CustomerListDTO> customers, long total, int page, int size, CustomerStatsDTO stats) {
        this.customers = customers;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.stats = stats;
    }

    // Getters and Setters
    public List<CustomerListDTO> getCustomers() { return customers; }
    public void setCustomers(List<CustomerListDTO> customers) { this.customers = customers; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public CustomerStatsDTO getStats() { return stats; }
    public void setStats(CustomerStatsDTO stats) { this.stats = stats; }
}
