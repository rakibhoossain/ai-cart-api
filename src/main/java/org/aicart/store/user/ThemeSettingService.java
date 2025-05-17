package org.aicart.store.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.aicart.store.user.dto.ThemeRequestDTO;
import org.aicart.store.user.entity.ShopThemeSetting;

@ApplicationScoped
public class ThemeSettingService {

    @Inject
    ThemeSettingRepository themeSettingRepository;

    public ShopThemeSetting findById(String id) {
        return themeSettingRepository.findById(id);
    }

    public ShopThemeSetting updateTheme(String id, ThemeRequestDTO dto) {
        ShopThemeSetting setting = id == null ? new ShopThemeSetting() : themeSettingRepository.findById(id);


        System.out.println(setting);


        if (dto.sections != null) {
            setting.sections = dto.sections;
        }

        return themeSettingRepository.save(setting);
    }
}
