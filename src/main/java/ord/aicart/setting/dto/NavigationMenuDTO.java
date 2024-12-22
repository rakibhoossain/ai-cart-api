package ord.aicart.setting.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NavigationMenuDTO {
    public Long id; // ID of the menu
    public String name; // Name of the menu (e.g., "Header Menu")
    public List<NavigationMenuItemDTO> items; // List of top-level menu items
    public LocalDateTime updatedAt; // Last update timestamp
}
