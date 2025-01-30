package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributeDTO {
    private String value;

    @JsonProperty("value_id")
    private int valueId;

    @JsonProperty("attribute_id")
    private int attributeId;

    @JsonProperty("attribute_name")
    private String attributeName;

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}

