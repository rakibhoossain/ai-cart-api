package org.aicart.store.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public class InventoryUpdateRequestDTO {
    
    @NotNull(message = "Variant ID is required")
    private Long variantId;
    
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
    
    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String notes;

    public enum AdjustmentType {
        INCREASE, DECREASE, SET, TRANSFER
    }

    // Constructors
    public InventoryUpdateRequestDTO() {}

    public InventoryUpdateRequestDTO(Long variantId, Long warehouseId, Integer quantity, 
                                   AdjustmentType adjustmentType, String reason, String notes) {
        this.variantId = variantId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.adjustmentType = adjustmentType;
        this.reason = reason;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public AdjustmentType getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(AdjustmentType adjustmentType) { this.adjustmentType = adjustmentType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
