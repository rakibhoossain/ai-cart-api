package org.aicart.store.product.dto.product;

public class AttributeDTO {
    private Integer attributeId; // Color, Size
    private Integer attributeValueId; // Red, Blue, XL

    // Getters and setters
    public Integer getAttributeId() { return attributeId; }
    public void setAttributeId(Integer attributeId) { this.attributeId = attributeId; }

    public Integer getAttributeValueId() { return attributeValueId; }
    public void setAttributeValueId(Integer attributeValueId) { this.attributeValueId = attributeValueId; }
}
