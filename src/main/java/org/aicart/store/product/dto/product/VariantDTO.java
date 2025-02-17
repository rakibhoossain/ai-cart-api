package org.aicart.store.product.dto.product;

import java.math.BigInteger;
import java.util.List;

public class VariantDTO {
    private Long id;         // Can be null
    private String sku;         // Can be null
    private Integer imageId;    // Can be null
    private List<AttributeDTO> attributes;
    private List<VariantPriceDTO> prices;
    private List<VariantStockDTO> stocks;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public List<AttributeDTO> getAttributes() { return attributes; }
    public void setAttributes(List<AttributeDTO> attributes) { this.attributes = attributes; }

    public List<VariantPriceDTO> getPrices() { return prices; }
    public void setPrices(List<VariantPriceDTO> prices) { this.prices = prices; }

    public List<VariantStockDTO> getStocks() { return stocks; }
    public void setStocks(List<VariantStockDTO> stocks) { this.stocks = stocks; }
}
