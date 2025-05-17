package org.aicart.store.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerDTO {

    @Transient
    public String id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid Email")
    public String email;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 30, message = "First name must be less than 30 characters")
    @JsonProperty("first_name")
    public String firstName;

    @Size(max = 30, message = "Last name must be less than 30 characters")
    @JsonProperty("last_name")
    public String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    public String phone;
}
