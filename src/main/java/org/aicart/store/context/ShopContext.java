package org.aicart.store.context;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ShopContext {
    private long shopId;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }
}
