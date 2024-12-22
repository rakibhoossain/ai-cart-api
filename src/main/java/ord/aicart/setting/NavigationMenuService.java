package ord.aicart.setting;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ord.aicart.setting.dto.NavigationMenuItemDTO;
import ord.aicart.setting.dto.PublicNavigationMenuItemDTO;
import ord.aicart.setting.entity.NavigationMenu;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;

import java.util.List;

@ApplicationScoped
public class NavigationMenuService {

    @Inject
    NavigationMenuMapper mapper;

    public List<PublicNavigationMenuItemDTO> getNavigationMenu(String name, String lang) {
        NavigationMenu menu = NavigationMenu.find("name", name).firstResult();

        if (menu == null) {
            throw new RuntimeException("Navigation menu not found");
        }

        return mapper.toPublicDTO(menu, lang);
    }

    public List<NavigationMenu> listAllMenus() {
        return NavigationMenu.listAll();
    }

    public NavigationMenu getMenuById(Long id) {
        return NavigationMenu.findById(id);
    }

    @Transactional
    public NavigationMenu createOrUpdateMenu(Long id, String name, List<NavigationMenuItemDTO> value) {
        NavigationMenu menu;
        if (id != null) {
            menu = NavigationMenu.findById(id);
            if (menu == null) {
                throw new RuntimeException("Menu not found");
            }
        } else {
            menu = new NavigationMenu();
        }

        menu.name = name;

        try {

            // Convert the value to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(value);

            // Create PGobject to store as JSONB
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(jsonValue);

            // Store the PGobject in the value field
            menu.value = pgObject.getValue();

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize menu items", e);
        }

        menu.persist();

        return menu;
    }

    @Transactional
    public void deleteMenu(Long id) {
        NavigationMenu.deleteById(id);
    }
}
