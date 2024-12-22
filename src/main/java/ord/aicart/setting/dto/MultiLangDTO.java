package ord.aicart.setting.dto;

import java.util.Map;

public class MultiLangDTO {
    public Map<String, String> localizedNames; // A map of locale -> name (e.g., {"en": "Home", "es": "Inicio"})

    // Get a name for a specific locale, or fallback to a default language
    public String getName(String locale, String fallbackLocale) {
        return localizedNames.getOrDefault(locale, localizedNames.get(fallbackLocale));
    }
}
