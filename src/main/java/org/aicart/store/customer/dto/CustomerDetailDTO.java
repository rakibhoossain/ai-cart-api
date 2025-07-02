package org.aicart.store.customer.dto;

import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.entity.Gender;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerDetailDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDateTime dateOfBirth;
    private Gender gender;
    private String company;
    private String jobTitle;
    private String languageCode;
    private String currencyCode;
    private String timezone;
    private String avatarUrl;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean newsletterSubscribe;
    private boolean emailSubscribe;
    private boolean phoneSubscribe;
    private boolean smsSubscribe;
    private Integer totalOrders;
    private Long totalSpent;
    private Long averageOrderValue;
    private Long lifetimeValue;
    private LocalDateTime firstOrderAt;
    private LocalDateTime lastOrderAt;
    private LocalDateTime lastActivityAt;
    private CustomerType customerType;
    private CustomerTier customerTier;
    private String tags;
    private String notes;
    private boolean accountLocked;
    private String accountLockedReason;
    private LocalDateTime accountLockedAt;
    private boolean taxExempt;
    private String taxExemptionReason;
    private String vatNumber;
    private String taxId;
    private List<CustomerAddressDTO> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CustomerDetailDTO() {}

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

    public LocalDateTime getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDateTime dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public boolean isNewsletterSubscribe() { return newsletterSubscribe; }
    public void setNewsletterSubscribe(boolean newsletterSubscribe) { this.newsletterSubscribe = newsletterSubscribe; }

    public boolean isEmailSubscribe() { return emailSubscribe; }
    public void setEmailSubscribe(boolean emailSubscribe) { this.emailSubscribe = emailSubscribe; }

    public boolean isPhoneSubscribe() { return phoneSubscribe; }
    public void setPhoneSubscribe(boolean phoneSubscribe) { this.phoneSubscribe = phoneSubscribe; }

    public boolean isSmsSubscribe() { return smsSubscribe; }
    public void setSmsSubscribe(boolean smsSubscribe) { this.smsSubscribe = smsSubscribe; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Long getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Long totalSpent) { this.totalSpent = totalSpent; }

    public Long getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(Long averageOrderValue) { this.averageOrderValue = averageOrderValue; }

    public Long getLifetimeValue() { return lifetimeValue; }
    public void setLifetimeValue(Long lifetimeValue) { this.lifetimeValue = lifetimeValue; }

    public LocalDateTime getFirstOrderAt() { return firstOrderAt; }
    public void setFirstOrderAt(LocalDateTime firstOrderAt) { this.firstOrderAt = firstOrderAt; }

    public LocalDateTime getLastOrderAt() { return lastOrderAt; }
    public void setLastOrderAt(LocalDateTime lastOrderAt) { this.lastOrderAt = lastOrderAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public CustomerTier getCustomerTier() { return customerTier; }
    public void setCustomerTier(CustomerTier customerTier) { this.customerTier = customerTier; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }

    public String getAccountLockedReason() { return accountLockedReason; }
    public void setAccountLockedReason(String accountLockedReason) { this.accountLockedReason = accountLockedReason; }

    public LocalDateTime getAccountLockedAt() { return accountLockedAt; }
    public void setAccountLockedAt(LocalDateTime accountLockedAt) { this.accountLockedAt = accountLockedAt; }

    public boolean isTaxExempt() { return taxExempt; }
    public void setTaxExempt(boolean taxExempt) { this.taxExempt = taxExempt; }

    public String getTaxExemptionReason() { return taxExemptionReason; }
    public void setTaxExemptionReason(String taxExemptionReason) { this.taxExemptionReason = taxExemptionReason; }

    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public List<CustomerAddressDTO> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddressDTO> addresses) { this.addresses = addresses; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getDisplayName() {
        String fullName = getFullName().trim();
        return fullName.isEmpty() ? email : fullName;
    }
}
