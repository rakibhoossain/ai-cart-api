package ord.aicart.setting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import ord.aicart.setting.dto.NavigationMenuItemDTO;
import ord.aicart.setting.dto.PublicNavigationMenuItemDTO;
import ord.aicart.setting.entity.NavigationMenu;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NavigationMenuMapper {

    public List<PublicNavigationMenuItemDTO> toPublicDTO(NavigationMenu entity, String lang, String fallbackLang) {
        // Deserialize JSONB and transform to the Public DTO
        return parseMenuItems(entity.value, lang, fallbackLang);
    }

    private List<PublicNavigationMenuItemDTO> parseMenuItems(String json, String lang, String fallbackLang) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the JSONB to a list of raw menu items
            List<NavigationMenuItemDTO> items = mapper.readValue(json, new TypeReference<List<NavigationMenuItemDTO>>() {});

            // Map to the public DTO while filtering names by the requested language
            return items.stream()
                    .map(item -> toPublicItemDTO(item, lang, fallbackLang))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse menu JSON", e);
        }
    }

    private PublicNavigationMenuItemDTO toPublicItemDTO(NavigationMenuItemDTO item, String lang, String fallbackLang) {
        PublicNavigationMenuItemDTO publicItem = new PublicNavigationMenuItemDTO();
        publicItem.id = item.id;
        publicItem.name = item.name.getName(lang, fallbackLang); // Extract the name in the requested language
        publicItem.url = item.url;
        publicItem.target = item.target;
        publicItem.image = item.image;
        publicItem.isMegaMenu = item.isMegaMenu;

        if (item.children != null) {
            publicItem.children = item.children.stream()
                    .map(child -> toPublicItemDTO(child, lang, fallbackLang))
                    .collect(Collectors.toList());
        }

        return publicItem;
    }
}
