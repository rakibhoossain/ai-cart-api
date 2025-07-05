package org.aicart.store.inventory.dto;

public class WarehouseStockDTO {
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Integer reserved;
    private Integer available;
    private Integer incoming;
    private Integer committed;

    public WarehouseStockDTO() {}

    public WarehouseStockDTO(Long warehouseId, String warehouseName, Integer quantity, 
                           Integer reserved, Integer available, Integer incoming, Integer committed) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.reserved = reserved;
        this.available = available;
        this.incoming = incoming;
        this.committed = committed;
    }

    // Getters and Setters
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReserved() { return reserved; }
    public void setReserved(Integer reserved) { this.reserved = reserved; }

    public Integer getAvailable() { return available; }
    public void setAvailable(Integer available) { this.available = available; }

    public Integer getIncoming() { return incoming; }
    public void setIncoming(Integer incoming) { this.incoming = incoming; }

    public Integer getCommitted() { return committed; }
    public void setCommitted(Integer committed) { this.committed = committed; }
}
