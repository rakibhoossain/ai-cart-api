package org.aicart.store.product.dto;

import org.aicart.store.product.entity.Attribute;

public class AttributeResponseDTO {

    private Long id;
    private String name;

    public AttributeResponseDTO(Attribute attribute) {
        this.id = attribute.id;
        this.name = attribute.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
