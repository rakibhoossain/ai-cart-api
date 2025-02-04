package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.aicart.store.product.dto.AttributeRequestDTO;
import org.aicart.store.product.dto.AttributeValueRequestDTO;
import org.aicart.store.product.entity.Attribute;
import org.aicart.store.product.entity.AttributeValue;

import java.util.List;

@ApplicationScoped
public class ProductAttributeService {

    public List<Attribute> getAttributes(int page, int size)
    {
        return Attribute.listAll();
    }

    public List<AttributeValue> getAttributeValues(long attributeId)
    {
        return AttributeValue.find("where attribute.id = ?1", attributeId).list();
    }

    @Transactional
    public Attribute createAttribute(AttributeRequestDTO attributeRequestDTO)
    {
        Attribute attribute = Attribute.find("where name = ?1", attributeRequestDTO.getName()).firstResult();
        if(attribute != null) {
            throw new ValidationException("Duplicate record found with name: " + attributeRequestDTO.getName());
        }

        Attribute newAttribute = new Attribute();
        newAttribute.name = attributeRequestDTO.getName();
        newAttribute.persist();

        return newAttribute;
    }


    @Transactional
    public AttributeValue createAttributeValue(long attributeId, AttributeValueRequestDTO attributeValueRequestDTO)
    {
        Attribute attribute = Attribute.find("where id = ?1", attributeId).firstResult();
        if(attribute == null) {
            throw new ValidationException("Invalid action");
        }

        AttributeValue attributeValue = AttributeValue.find("where attribute.id = ?1 AND value = ?2", attributeId, attributeValueRequestDTO.getValue()).firstResult();

        if(attributeValue != null) {
            throw new ValidationException("Duplicate record found with value: " + attributeValueRequestDTO.getValue());
        }

        AttributeValue newAttributeValue = new AttributeValue();
        newAttributeValue.attribute = attribute;
        newAttributeValue.value = attributeValueRequestDTO.getValue();
        newAttributeValue.color = attributeValueRequestDTO.getColor();
        newAttributeValue.imageId = attributeValueRequestDTO.getImageId();
        newAttributeValue.persist();

        return newAttributeValue;
    }

}
