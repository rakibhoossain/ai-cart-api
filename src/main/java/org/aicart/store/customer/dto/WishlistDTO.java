package org.aicart.store.customer.dto;

import org.aicart.store.customer.entity.Wishlist;
import org.aicart.store.product.dto.ProductItemDTO;

import java.time.LocalDateTime;

public class WishlistDTO {
    public Long id;
    public ProductItemDTO product;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public static WishlistDTO fromEntity(Wishlist wishlist, ProductItemDTO productDTO) {
        WishlistDTO dto = new WishlistDTO();
        dto.id = wishlist.id;
        dto.product = productDTO;
        dto.createdAt = wishlist.createdAt;
        dto.updatedAt = wishlist.updatedAt;
        return dto;
    }
}
