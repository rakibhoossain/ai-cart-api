package org.aicart.store.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.entity.ShopThemeSetting;

@ApplicationScoped
public class ThemeSettingRepository {
    @Inject
    EntityManager em;

    public ShopThemeSetting findById(String id) {
        return em.find(ShopThemeSetting.class, id);
    }

    @Transactional
    public ShopThemeSetting save(ShopThemeSetting setting) {
        if (setting.id == null) {
            setting.shop = Shop.findById(1); // Shop
            em.persist(setting);
            return setting;
        } else {
            return em.merge(setting);
        }
    }
}
