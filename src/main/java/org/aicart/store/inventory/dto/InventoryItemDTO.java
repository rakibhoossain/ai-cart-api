package org.aicart.store.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;

public class InventoryItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private Long variantId;
    private String variantSku;
    private List<VariantAttributeDTO> variantAttributes;
    private Integer currentStock;
    private Integer reservedStock;
    private Integer availableStock;
    private Integer incomingStock;
    private Integer committedStock;
    private Integer onHandStock;
    private List<WarehouseStockDTO> warehouseStocks;
    private Integer lowStockThreshold;
    private Boolean trackQuantity;
    private Boolean continueSellingWhenOutOfStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public InventoryItemDTO() {}

    public InventoryItemDTO(Long id, Long productId, String productName, String productSlug,
                           Long variantId, String variantSku, Integer currentStock, Integer reservedStock,
                           Integer availableStock, Boolean trackQuantity, Boolean continueSellingWhenOutOfStock) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.variantId = variantId;
        this.variantSku = variantSku;
        this.currentStock = currentStock;
        this.reservedStock = reservedStock;
        this.availableStock = availableStock;
        this.trackQuantity = trackQuantity;
        this.continueSellingWhenOutOfStock = continueSellingWhenOutOfStock;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSlug() { return productSlug; }
    public void setProductSlug(String productSlug) { this.productSlug = productSlug; }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public String getVariantSku() { return variantSku; }
    public void setVariantSku(String variantSku) { this.variantSku = variantSku; }

    public List<VariantAttributeDTO> getVariantAttributes() { return variantAttributes; }
    public void setVariantAttributes(List<VariantAttributeDTO> variantAttributes) { this.variantAttributes = variantAttributes; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getReservedStock() { return reservedStock; }
    public void setReservedStock(Integer reservedStock) { this.reservedStock = reservedStock; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Integer getIncomingStock() { return incomingStock; }
    public void setIncomingStock(Integer incomingStock) { this.incomingStock = incomingStock; }

    public Integer getCommittedStock() { return committedStock; }
    public void setCommittedStock(Integer committedStock) { this.committedStock = committedStock; }

    public Integer getOnHandStock() { return onHandStock; }
    public void setOnHandStock(Integer onHandStock) { this.onHandStock = onHandStock; }

    public List<WarehouseStockDTO> getWarehouseStocks() { return warehouseStocks; }
    public void setWarehouseStocks(List<WarehouseStockDTO> warehouseStocks) { this.warehouseStocks = warehouseStocks; }

    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public Boolean getTrackQuantity() { return trackQuantity; }
    public void setTrackQuantity(Boolean trackQuantity) { this.trackQuantity = trackQuantity; }

    public Boolean getContinueSellingWhenOutOfStock() { return continueSellingWhenOutOfStock; }
    public void setContinueSellingWhenOutOfStock(Boolean continueSellingWhenOutOfStock) { this.continueSellingWhenOutOfStock = continueSellingWhenOutOfStock; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
