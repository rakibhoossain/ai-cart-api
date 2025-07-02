package org.aicart.store.customer.dto;

import jakarta.validation.constraints.Size;

public class CustomerAddressUpdateDTO {
    
    @Size(max = 20, message = "Address type must be less than 20 characters")
    private String type; // BILLING, SHIPPING, BOTH

    @Size(max = 30, message = "First name must be less than 30 characters")
    private String firstName;

    @Size(max = 30, message = "Last name must be less than 30 characters")
    private String lastName;

    @Size(max = 100, message = "Company must be less than 100 characters")
    private String company;

    @Size(max = 255, message = "Address line 1 must be less than 255 characters")
    private String line1;

    @Size(max = 255, message = "Address line 2 must be less than 255 characters")
    private String line2;

    @Size(max = 30, message = "City must be less than 30 characters")
    private String city;

    @Size(max = 30, message = "State must be less than 30 characters")
    private String state;

    @Size(max = 50, message = "Country must be less than 50 characters")
    private String country;

    @Size(max = 2, message = "Country code must be 2 characters")
    private String countryCode; // ISO 3166-1 alpha-2

    @Size(max = 20, message = "Postal code must be less than 20 characters")
    private String postalCode;

    @Size(max = 20, message = "Phone must be less than 20 characters")
    private String phone;

    private Boolean isDefault;

    // Constructors
    public CustomerAddressUpdateDTO() {}

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

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean isDefault() { return isDefault; }
    public void setDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
