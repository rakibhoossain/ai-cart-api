package org.aicart.store.customer.dto;

import java.util.List;

public class WishlistResponseDTO {
    public List<WishlistDTO> items;
    public long totalCount;
    public int page;
    public int size;
    public boolean hasMore;

    public WishlistResponseDTO() {}

    public WishlistResponseDTO(List<WishlistDTO> items, long totalCount, int page, int size) {
        this.items = items;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.hasMore = (page + 1) * size < totalCount;
    }
}
