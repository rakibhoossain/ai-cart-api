package org.aicart.store.product.dto.product;

import java.util.List;

public class ProductCreateRequestDTO {
    private List<Long> images;
    private List<Integer> categories;  // Can be null
    private String name;
    private String description;
    private List<VariantDTO> variants;

    // Getters and setters
    public List<Long> getImages() { return images; }
    public void setImages(List<Long> images) { this.images = images; }

    public List<Integer> getCategories() { return categories; }
    public void setCategories(List<Integer> categories) { this.categories = categories; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<VariantDTO> getVariants() { return variants; }
    public void setVariants(List<VariantDTO> variants) { this.variants = variants; }
}

