package store.aicart.product.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

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

    public static ProductVariantDTO parseSingleJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // Parse and return the list of ProductVariantDTO
        return mapper.readValue(json, new TypeReference<ProductVariantDTO>() {});
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }
}
