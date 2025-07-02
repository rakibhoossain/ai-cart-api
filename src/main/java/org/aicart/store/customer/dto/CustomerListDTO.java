package org.aicart.store.customer.dto;

import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;

import java.time.LocalDateTime;

public class CustomerListDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private CustomerType customerType;
    private CustomerTier customerTier;
    private boolean emailVerified;
    private boolean accountLocked;
    private Integer totalOrders;
    private Long totalSpent;
    private Long lifetimeValue;
    private LocalDateTime lastOrderAt;
    private LocalDateTime lastActivityAt;
    private LocalDateTime createdAt;

    // Constructors
    public CustomerListDTO() {}

    public CustomerListDTO(Long id, String firstName, String lastName, String email, String phone, 
                          String company, CustomerType customerType, CustomerTier customerTier, 
                          boolean emailVerified, boolean accountLocked, Integer totalOrders, 
                          Long totalSpent, Long lifetimeValue, LocalDateTime lastOrderAt, 
                          LocalDateTime lastActivityAt, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.customerType = customerType;
        this.customerTier = customerTier;
        this.emailVerified = emailVerified;
        this.accountLocked = accountLocked;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.lifetimeValue = lifetimeValue;
        this.lastOrderAt = lastOrderAt;
        this.lastActivityAt = lastActivityAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public CustomerTier getCustomerTier() { return customerTier; }
    public void setCustomerTier(CustomerTier customerTier) { this.customerTier = customerTier; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Long getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Long totalSpent) { this.totalSpent = totalSpent; }

    public Long getLifetimeValue() { return lifetimeValue; }
    public void setLifetimeValue(Long lifetimeValue) { this.lifetimeValue = lifetimeValue; }

    public LocalDateTime getLastOrderAt() { return lastOrderAt; }
    public void setLastOrderAt(LocalDateTime lastOrderAt) { this.lastOrderAt = lastOrderAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getDisplayName() {
        String fullName = getFullName().trim();
        return fullName.isEmpty() ? email : fullName;
    }
}
