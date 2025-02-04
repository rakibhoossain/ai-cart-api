package org.aicart.store.product.dto;

import org.aicart.store.product.entity.AttributeValue;

public class AttributeValueResponseDTO {
    private Long id;
    private String value;
    private String color;
    private Long imageId;


    public AttributeValueResponseDTO(AttributeValue attributeValue) {
        this.id = attributeValue.id;
        this.value = attributeValue.value;
        this.color = attributeValue.color;
        this.imageId = attributeValue.imageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
}
