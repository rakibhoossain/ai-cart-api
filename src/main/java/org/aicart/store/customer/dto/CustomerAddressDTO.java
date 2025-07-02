package org.aicart.store.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CustomerAddressDTO {

    @Transient
    public Long id;

    @Size(max = 20, message = "Address type must be less than 20 characters")
    public String type; // BILLING, SHIPPING, BOTH

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 30, message = "First name must be less than 30 characters")
    @JsonProperty("first_name")
    public String firstName;

    @Size(max = 30, message = "Last name must be less than 30 characters")
    @JsonProperty("last_name")
    public String lastName;

    @Size(max = 100, message = "Company must be less than 100 characters")
    public String company;

    @NotBlank(message = "Address line 1 cannot be blank")
    @Size(max = 255, message = "Address line 1 must be less than 255 characters")
    public String line1;

    @Size(max = 255, message = "Address line 2 must be less than 255 characters")
    public String line2;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 30, message = "City must be less than 30 characters")
    public String city;

    @NotBlank(message = "State cannot be blank")
    @Size(max = 30, message = "State must be less than 30 characters")
    public String state;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 50, message = "Country must be less than 50 characters")
    public String country;

    @NotBlank(message = "Postal code cannot be blank")
    @Size(max = 20, message = "Postal code must be less than 20 characters")
    @JsonProperty("postal_code")
    public String postalCode;

    @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid country code format")
    @JsonProperty("country_code")
    public String countryCode; // ISO 3166-1 alpha-2

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    public String phone;

    @JsonProperty("is_default")
    public boolean isDefault = false;

    @JsonProperty("created_at")
    public LocalDateTime createdAt;

    @JsonProperty("updated_at")
    public LocalDateTime updatedAt;

    // Helper methods
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getFormattedAddress() {
        StringBuilder address = new StringBuilder();

        if (line1 != null && !line1.trim().isEmpty()) {
            address.append(line1);
        }

        if (line2 != null && !line2.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(line2);
        }

        if (city != null && !city.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }

        if (state != null && !state.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }

        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(postalCode);
        }

        if (country != null && !country.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(country);
        }

        return address.toString();
    }
}
