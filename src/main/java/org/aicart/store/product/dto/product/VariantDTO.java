package org.aicart.store.product.dto.product;

import java.math.BigInteger;
import java.util.List;

public class VariantDTO {
    private Integer id;         // Can be null
    private String sku;         // Can be null
    private Integer imageId;    // Can be null
    private BigInteger price;
    private BigInteger comparePrice;
    private BigInteger purchasePrice;
    private List<AttributeDTO> attributes;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public BigInteger getComparePrice() { return comparePrice; }
    public void setComparePrice(BigInteger comparePrice) { this.comparePrice = comparePrice; }

    public BigInteger getPrice() { return price; }
    public void setPrice(BigInteger price) { this.price = price; }

    public BigInteger getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigInteger purchasePrice) { this.purchasePrice = purchasePrice; }

    public List<AttributeDTO> getAttributes() { return attributes; }
    public void setAttributes(List<AttributeDTO> attributes) { this.attributes = attributes; }
}
