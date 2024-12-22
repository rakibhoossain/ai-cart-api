package ord.aicart.setting.dto;

import java.util.List;

public class PublicNavigationMenuItemDTO {
    public String id; // Unique identifier for the menu item
    public String name; // Name in the requested language
    public String url; // URL for the menu item
    public String target; // Link target (_self, _blank, etc.)
    public String image; // Optional image associated with the menu item
    public boolean isMegaMenu; // Indicates if the menu is a mega menu
    public List<PublicNavigationMenuItemDTO> children; // Nested children menu items
}
