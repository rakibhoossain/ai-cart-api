package org.aicart.store.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.entity.Gender;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerCreateRequestDTO {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    private String phone;
    private LocalDateTime dateOfBirth;
    private Gender gender;
    private String company;
    private String jobTitle;
    private String languageCode = "en";
    private String currencyCode = "USD";
    private String timezone = "UTC";
    private String avatarUrl;
    private String password; // For creating account with password
    
    // Marketing preferences
    private boolean newsletterSubscribe = false;
    private boolean emailSubscribe = false;
    private boolean phoneSubscribe = false;
    private boolean smsSubscribe = false;
    
    // Customer classification
    private CustomerType customerType = CustomerType.REGULAR;
    private CustomerTier customerTier = CustomerTier.BRONZE;
    private String tags;
    private String notes;
    
    // Tax information
    private boolean taxExempt = false;
    private String taxExemptionReason;
    private String vatNumber;
    private String taxId;
    
    // Addresses
    @Valid
    private List<CustomerAddressCreateDTO> addresses;
    
    // Account settings
    private boolean sendWelcomeEmail = true;
    private boolean verifyEmail = false; // Whether to mark email as verified immediately

    // Constructors
    public CustomerCreateRequestDTO() {}

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isNewsletterSubscribe() { return newsletterSubscribe; }
    public void setNewsletterSubscribe(boolean newsletterSubscribe) { this.newsletterSubscribe = newsletterSubscribe; }

    public boolean isEmailSubscribe() { return emailSubscribe; }
    public void setEmailSubscribe(boolean emailSubscribe) { this.emailSubscribe = emailSubscribe; }

    public boolean isPhoneSubscribe() { return phoneSubscribe; }
    public void setPhoneSubscribe(boolean phoneSubscribe) { this.phoneSubscribe = phoneSubscribe; }

    public boolean isSmsSubscribe() { return smsSubscribe; }
    public void setSmsSubscribe(boolean smsSubscribe) { this.smsSubscribe = smsSubscribe; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public CustomerTier getCustomerTier() { return customerTier; }
    public void setCustomerTier(CustomerTier customerTier) { this.customerTier = customerTier; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isTaxExempt() { return taxExempt; }
    public void setTaxExempt(boolean taxExempt) { this.taxExempt = taxExempt; }

    public String getTaxExemptionReason() { return taxExemptionReason; }
    public void setTaxExemptionReason(String taxExemptionReason) { this.taxExemptionReason = taxExemptionReason; }

    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public List<CustomerAddressCreateDTO> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddressCreateDTO> addresses) { this.addresses = addresses; }

    public boolean isSendWelcomeEmail() { return sendWelcomeEmail; }
    public void setSendWelcomeEmail(boolean sendWelcomeEmail) { this.sendWelcomeEmail = sendWelcomeEmail; }

    public boolean isVerifyEmail() { return verifyEmail; }
    public void setVerifyEmail(boolean verifyEmail) { this.verifyEmail = verifyEmail; }

    // Nested DTO for address creation
    public static class CustomerAddressCreateDTO {
        @NotBlank(message = "Address type is required")
        private String type; // BILLING, SHIPPING, BOTH
        
        private String firstName;
        private String lastName;
        private String company;
        
        @NotBlank(message = "Address line 1 is required")
        private String line1;
        
        private String line2;
        
        @NotBlank(message = "City is required")
        private String city;
        
        @NotBlank(message = "State is required")
        private String state;
        
        @NotBlank(message = "Country is required")
        private String country;
        
        @NotBlank(message = "Postal code is required")
        private String postalCode;
        
        private String phone;
        private boolean isDefault = false;

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public String getLine1() { return line1; }
        public void setLine1(String line1) { this.line1 = line1; }

        public String getLine2() { return line2; }
        public void setLine2(String line2) { this.line2 = line2; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    }
}
