package org.aicart.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.aicart.authentication.validation.StrongPassword;

public class ChangePasswordDTO {

    @NotBlank(message = "Current Password is required")
    @Size(min = 8, message = "Current Password must be at least 8 characters long")
    @JsonProperty("current_password")
    private String currentPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @StrongPassword
    private String password;

    @JsonProperty("shop_id")
    private long shopId;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getShopId() { return shopId; }

    public void setShopId(long shopId) { this.shopId = shopId; }
}
