package org.aicart.store.filter;


import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.aicart.store.context.ShopContext;

@Provider
@Priority(1)
public class ShopIdFilter implements ContainerRequestFilter {

    @Inject
    ShopContext shopContext;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String shopId = ctx.getHeaderString("Shop-Id");
        if (shopId != null && !shopId.isBlank()) {
            shopContext.setShopId(Long.parseLong(shopId));
        }
    }
}
