package org.aicart.store.customer.dto;

import jakarta.validation.constraints.NotNull;

public class WishlistRequestDTO {
    
    @NotNull(message = "Product ID is required")
    public Long productId;

    public WishlistRequestDTO() {}

    public WishlistRequestDTO(Long productId) {
        this.productId = productId;
    }
}
