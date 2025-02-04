package org.aicart.store.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AttributeValueRequestDTO {

    @NotBlank(message = "Value cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String value;

    @Size(min = 2, max = 50, message = "Color must be between 2 and 50 characters")
    private String color;

    private Long imageId;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
}
