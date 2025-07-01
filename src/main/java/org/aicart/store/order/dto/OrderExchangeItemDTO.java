package org.aicart.store.order.dto;

public class OrderExchangeItemDTO {
    private Long id;
    private Long originalOrderItemId;
    private String originalProductName;
    private String originalVariantName;
    private Long newVariantId;
    private String newProductName;
    private String newVariantName;
    private Integer quantity;
    private String reason;

    // Constructors
    public OrderExchangeItemDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOriginalOrderItemId() { return originalOrderItemId; }
    public void setOriginalOrderItemId(Long originalOrderItemId) { this.originalOrderItemId = originalOrderItemId; }

    public String getOriginalProductName() { return originalProductName; }
    public void setOriginalProductName(String originalProductName) { this.originalProductName = originalProductName; }

    public String getOriginalVariantName() { return originalVariantName; }
    public void setOriginalVariantName(String originalVariantName) { this.originalVariantName = originalVariantName; }

    public Long getNewVariantId() { return newVariantId; }
    public void setNewVariantId(Long newVariantId) { this.newVariantId = newVariantId; }

    public String getNewProductName() { return newProductName; }
    public void setNewProductName(String newProductName) { this.newProductName = newProductName; }

    public String getNewVariantName() { return newVariantName; }
    public void setNewVariantName(String newVariantName) { this.newVariantName = newVariantName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
