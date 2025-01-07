package org.aicart.setting.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aicart.setting.entity.NavigationMenu;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NavigationMenuDTO {
    public Long id; // ID of the menu
    public String name; // Name of the menu (e.g., "Header Menu")
    public List<NavigationMenuItemDTO> items; // List of top-level menu items
    public LocalDateTime createdAt; // Created timestamp
    public LocalDateTime updatedAt; // Last update timestamp

    public NavigationMenuDTO(Long id, String name, List<NavigationMenuItemDTO> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Static method to convert the entity into a DTO
    public static NavigationMenuDTO fromEntity(NavigationMenu entity) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<NavigationMenuItemDTO> menuItems;

        try {
            menuItems = objectMapper.readValue(entity.value, objectMapper.getTypeFactory().constructCollectionType(List.class, NavigationMenuItemDTO.class));
        } catch (JsonProcessingException e) {
            menuItems = Collections.emptyList();
        }

        return new NavigationMenuDTO(entity.id, entity.name, menuItems, entity.createdAt, entity.updatedAt);
    }
}



