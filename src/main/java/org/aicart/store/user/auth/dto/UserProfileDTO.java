package org.aicart.store.user.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserProfileDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Minimum 2 characters long")
    @Size(max = 30, message = "Maximum 30 characters long")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
