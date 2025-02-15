package org.aicart.store.product.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VariantStockDTO {
    @JsonProperty("warehouse_id")
    private long warehouseId;
    private int quantity;

    public long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
