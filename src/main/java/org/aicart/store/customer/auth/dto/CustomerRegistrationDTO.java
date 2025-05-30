package org.aicart.store.customer.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import org.aicart.authentication.validation.StrongPassword;
import org.aicart.store.customer.auth.validation.UniqueEmail;

@UniqueEmail
public class CustomerRegistrationDTO {

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

    @Pattern(regexp = "^\\+[1-9]\\d{0,14}$", message = "Invalid phone number format")
    public String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @StrongPassword
    public String password;
}
