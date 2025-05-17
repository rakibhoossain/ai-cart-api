package org.aicart.store.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerAddressDTO {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 30, message = "First name must be less than 30 characters")
    @JsonProperty("first_name")
    public String firstName;

    @Size(max = 30, message = "Last name must be less than 30 characters")
    @JsonProperty("last_name")
    public String lastName;

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

    @NotBlank(message = "Postal code cannot be blank")
    @Size(max = 20, message = "Postal code must be less than 20 characters")
    @JsonProperty("postal_code")
    public String postalCode;

    @NotBlank(message = "Country code cannot be blank")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid country code format")
    @JsonProperty("country_code")
    public String countryCode; // ISO 3166-1 alpha-2

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    public String phone;
}
