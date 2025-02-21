package org.aicart.store.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ValueNameRequest {
    @NotBlank(message = "Name must not be null or blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
