package org.aicart.store.customer.mapper;

import org.aicart.store.customer.dto.CustomerTagDTO;
import org.aicart.store.customer.entity.CustomerTag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomerTagMapper {

    public static CustomerTagDTO toDto(CustomerTag tag) {
        if (tag == null) {
            return null;
        }

        CustomerTagDTO dto = new CustomerTagDTO();
        dto.setId(tag.id);
        dto.setName(tag.name);
        dto.setSlug(tag.slug);
        dto.setColor(tag.color);
        dto.setDescription(tag.description);
        dto.setCustomerCount((long) tag.customers.size());
        dto.setCreatedAt(tag.createdAt);
        dto.setUpdatedAt(tag.updatedAt);

        return dto;
    }

    public static List<CustomerTagDTO> toDtoList(List<CustomerTag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(CustomerTagMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Set<CustomerTagDTO> toDtoSet(Set<CustomerTag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(CustomerTagMapper::toDto)
                .collect(Collectors.toSet());
    }

    public static CustomerTag toEntity(CustomerTagDTO dto) {
        if (dto == null) {
            return null;
        }

        CustomerTag tag = new CustomerTag();
        tag.id = dto.getId();
        tag.name = dto.getName();
        tag.slug = dto.getSlug();
        tag.color = dto.getColor();
        tag.description = dto.getDescription();
        tag.createdAt = dto.getCreatedAt();
        tag.updatedAt = dto.getUpdatedAt();

        return tag;
    }

    public static void updateEntityFromDto(CustomerTag tag, CustomerTagDTO dto) {
        if (tag == null || dto == null) {
            return;
        }

        tag.name = dto.getName();
        tag.color = dto.getColor();
        tag.description = dto.getDescription();
        // Note: slug is usually generated, not directly updated from DTO
        // Note: createdAt should not be updated
        // updatedAt is handled by @PreUpdate
    }
}
