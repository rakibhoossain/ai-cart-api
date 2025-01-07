package org.aicart.setting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.setting.dto.NavigationMenuItemDTO;
import org.aicart.setting.dto.PublicNavigationMenuItemDTO;
import org.aicart.setting.entity.NavigationMenu;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NavigationMenuMapper {

    public List<PublicNavigationMenuItemDTO> toPublicDTO(NavigationMenu entity, String lang) {
        // Deserialize JSONB and transform to the Public DTO
        return parseMenuItems(entity.value, lang);
    }

    private List<PublicNavigationMenuItemDTO> parseMenuItems(String json, String lang) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the JSONB to a list of raw menu items
            List<NavigationMenuItemDTO> items = mapper.readValue(json, new TypeReference<List<NavigationMenuItemDTO>>() {});

            // Map to the public DTO while filtering names by the requested language
            return items.stream()
                    .map(item -> toPublicItemDTO(item, lang))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse menu JSON", e);
        }
    }

    private PublicNavigationMenuItemDTO toPublicItemDTO(NavigationMenuItemDTO item, String lang) {
        PublicNavigationMenuItemDTO publicItem = new PublicNavigationMenuItemDTO();
        publicItem.id = item.id;
        publicItem.name = item.getName(lang, item.name); // Extract the name in the requested language
        publicItem.url = item.url;
        publicItem.target = item.target;
        publicItem.image = item.image;
        publicItem.isMegaMenu = item.isMegaMenu;

        if (item.children != null) {
            publicItem.children = item.children.stream()
                    .map(child -> toPublicItemDTO(child, lang))
                    .collect(Collectors.toList());
        }

        return publicItem;
    }
}
