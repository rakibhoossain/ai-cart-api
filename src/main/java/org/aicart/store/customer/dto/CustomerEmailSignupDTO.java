package org.aicart.store.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for email-only customer creation (newsletter signup, email capture forms)
 * This is the minimal customer creation for lead generation
 */
public class CustomerEmailSignupDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;
    
    // Optional geolocation data from frontend
    private String country;
    private String city;
    private String countryCode; // ISO 3166-1 alpha-2
    
    // Optional marketing preferences
    private boolean newsletterSubscribe = true; // Default to true for newsletter signup
    private boolean emailSubscribe = true; // Default to true for email marketing
    
    // Optional source tracking
    private String source; // "newsletter", "popup", "footer", etc.
    private String campaign; // Campaign tracking
    private String referrer; // Referrer URL
    
    // Constructors
    public CustomerEmailSignupDTO() {}
    
    public CustomerEmailSignupDTO(String email) {
        this.email = email;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public boolean isNewsletterSubscribe() { return newsletterSubscribe; }
    public void setNewsletterSubscribe(boolean newsletterSubscribe) { this.newsletterSubscribe = newsletterSubscribe; }
    
    public boolean isEmailSubscribe() { return emailSubscribe; }
    public void setEmailSubscribe(boolean emailSubscribe) { this.emailSubscribe = emailSubscribe; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getCampaign() { return campaign; }
    public void setCampaign(String campaign) { this.campaign = campaign; }
    
    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
}
