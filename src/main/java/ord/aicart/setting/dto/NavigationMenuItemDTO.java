package ord.aicart.setting.dto;

import java.util.List;
import java.util.Map;

public class NavigationMenuItemDTO {
    public String id; // Unique identifier for the menu item
    public String url; // URL for the menu item
    public String target; // Link target (_self, _blank, etc.)
    public String image; // Optional image associated with the menu item
    public boolean isMegaMenu; // Indicates if the menu is a mega menu
    public String name; // Default name
    public Map<String, String> locale; // Name with multi-language support
    public List<NavigationMenuItemDTO> children; // Nested children menu items

    // Get a name for a specific locale, or fallback to a default language
    public String getName(String lang, String name) {
        return locale.getOrDefault(lang, name);
    }
}
