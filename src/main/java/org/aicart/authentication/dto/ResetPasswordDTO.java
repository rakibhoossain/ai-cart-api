package org.aicart.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.aicart.authentication.validation.StrongPassword;

public class ResetPasswordDTO {

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @StrongPassword
    private String password;

    @NotBlank(message = "Token is required")
    private String token;

    @JsonProperty("shop_id")
    private long shopId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getShopId() { return shopId; }

    public void setShopId(long shopId) { this.shopId = shopId; }
}
