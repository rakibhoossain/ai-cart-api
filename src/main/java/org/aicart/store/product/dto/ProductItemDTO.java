package org.aicart.store.product.dto;

import java.util.Collections;
import java.util.List;

public class ProductItemDTO {
    private Long id;
    private String name;
    private String slug;
    private Long localeId;
    private String sku;
    private List<ImageDTO> images;
    private List<CategoryDTO> categories;
    private List<ProductVariantDTO> variants;

    // Constructor
    public ProductItemDTO(Long id, String name, String slug, Long localeId, String localeName, String sku, String imagesJson, String categoriesJson, String variantsJson) {
        this.id = id;
        this.name = localeId != null ? localeName : name;
        this.slug = slug;
        this.localeId = localeId;
        this.sku = sku;


        try {
            this.categories = CategoryDTO.parseJsonToHierarchy(categoriesJson);
        } catch (Exception e) {
            this.categories = Collections.emptyList();
        }


        try {
            this.variants = ProductVariantDTO.parseJson(variantsJson);
        } catch (Exception e) {
            this.variants = Collections.emptyList();
        }

        try {
            this.images = ImageDTO.parseJson(imagesJson);
        } catch (Exception e) {
            this.images = Collections.emptyList();
        }
    }

    public ProductItemDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public List<ImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<ProductVariantDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantDTO> variants) {
        this.variants = variants;
    }
}
