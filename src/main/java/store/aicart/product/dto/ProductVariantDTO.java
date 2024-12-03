package store.aicart.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

class VariantPriceDTO {
    private Integer price;
    private Integer discount;

    @JsonProperty("tax_rate")
    private Integer taxRate;

    @JsonProperty("currency_id")
    private Integer currencyId;

    // Getters and Setters
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Integer taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
}

class AttributeDTO {
    private String value;

    @JsonProperty("value_id")
    private int valueId;

    @JsonProperty("attribute_id")
    private int attributeId;

    @JsonProperty("attribute_name")
    private String attributeName;

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}

public class ProductVariantDTO {
    private Long id;
    private String sku;
    private VariantPriceDTO price;
    private Long stock;
    private String[] images;
    private AttributeDTO[] attributes;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public VariantPriceDTO getPrice() {
        return price;
    }

    public void setPrice(VariantPriceDTO price) {
        this.price = price;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public AttributeDTO[] getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributeDTO[] attributes) {
        this.attributes = attributes;
    }

    // Method to parse JSON into a list of ProductVariantDTO
    public static List<ProductVariantDTO> parseJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // Parse and return the list of ProductVariantDTO
        return mapper.readValue(json, new TypeReference<List<ProductVariantDTO>>() {});
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }
}
