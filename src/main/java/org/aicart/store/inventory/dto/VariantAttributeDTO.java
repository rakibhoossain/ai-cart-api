package org.aicart.store.inventory.dto;

public class VariantAttributeDTO {
    private Long attributeId;
    private String attributeName;
    private Long valueId;
    private String value;
    private String color;

    public VariantAttributeDTO() {}

    public VariantAttributeDTO(Long attributeId, String attributeName, Long valueId, String value, String color) {
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.valueId = valueId;
        this.value = value;
        this.color = color;
    }

    // Getters and Setters
    public Long getAttributeId() { return attributeId; }
    public void setAttributeId(Long attributeId) { this.attributeId = attributeId; }

    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public Long getValueId() { return valueId; }
    public void setValueId(Long valueId) { this.valueId = valueId; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
