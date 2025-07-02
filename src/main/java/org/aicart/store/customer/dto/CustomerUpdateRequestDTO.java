package org.aicart.store.customer.dto;

import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.entity.Gender;

import java.time.LocalDateTime;

public class CustomerUpdateRequestDTO {
    
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
    
    // Marketing preferences
    private Boolean newsletterSubscribe;
    private Boolean emailSubscribe;
    private Boolean phoneSubscribe;
    private Boolean smsSubscribe;
    
    // Customer classification
    private CustomerType customerType;
    private CustomerTier customerTier;
    private String tags;
    private String notes;
    
    // Account management
    private Boolean accountLocked;
    private String accountLockedReason;
    
    // Tax information
    private Boolean taxExempt;
    private String taxExemptionReason;
    private String vatNumber;
    private String taxId;
    
    // Update metadata
    private String updateReason; // Why the customer was updated
    private Boolean notifyCustomer = false; // Whether to notify customer of changes

    // Constructors
    public CustomerUpdateRequestDTO() {}

    // Getters and Setters
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

    public Boolean getNewsletterSubscribe() { return newsletterSubscribe; }
    public void setNewsletterSubscribe(Boolean newsletterSubscribe) { this.newsletterSubscribe = newsletterSubscribe; }

    public Boolean getEmailSubscribe() { return emailSubscribe; }
    public void setEmailSubscribe(Boolean emailSubscribe) { this.emailSubscribe = emailSubscribe; }

    public Boolean getPhoneSubscribe() { return phoneSubscribe; }
    public void setPhoneSubscribe(Boolean phoneSubscribe) { this.phoneSubscribe = phoneSubscribe; }

    public Boolean getSmsSubscribe() { return smsSubscribe; }
    public void setSmsSubscribe(Boolean smsSubscribe) { this.smsSubscribe = smsSubscribe; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public CustomerTier getCustomerTier() { return customerTier; }
    public void setCustomerTier(CustomerTier customerTier) { this.customerTier = customerTier; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getAccountLocked() { return accountLocked; }
    public void setAccountLocked(Boolean accountLocked) { this.accountLocked = accountLocked; }

    public String getAccountLockedReason() { return accountLockedReason; }
    public void setAccountLockedReason(String accountLockedReason) { this.accountLockedReason = accountLockedReason; }

    public Boolean getTaxExempt() { return taxExempt; }
    public void setTaxExempt(Boolean taxExempt) { this.taxExempt = taxExempt; }

    public String getTaxExemptionReason() { return taxExemptionReason; }
    public void setTaxExemptionReason(String taxExemptionReason) { this.taxExemptionReason = taxExemptionReason; }

    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getUpdateReason() { return updateReason; }
    public void setUpdateReason(String updateReason) { this.updateReason = updateReason; }

    public Boolean getNotifyCustomer() { return notifyCustomer; }
    public void setNotifyCustomer(Boolean notifyCustomer) { this.notifyCustomer = notifyCustomer; }
}
