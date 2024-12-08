package store.aicart.order.dto;

import store.aicart.product.dto.CategoryDTO;
import store.aicart.product.dto.ProductVariantDTO;

import java.util.Collections;
import java.util.List;

public class CartItemDTO {
    private Long id;
    private Long productId;
    private Long variantId;
    private String name;
    private String slug;
    private Long localeId;
    private String sku;
    private int quantity;
    private List<CategoryDTO> categories;
    private ProductVariantDTO variant;


    // Constructor
    public CartItemDTO(Long id, Long productId, Long variantId, String name, String slug, Long localeId, String localeName, String sku, int quantity, String categoriesJson, String variantJson) {
        this.id = id;
        this.productId = productId;
        this.variantId = variantId;
        this.name = localeId != null ? localeName : name;
        this.slug = slug;
        this.localeId = localeId;
        this.sku = sku;
        this.quantity = quantity;

        try {
            this.categories = CategoryDTO.parseJsonToHierarchy(categoriesJson);
        } catch (Exception e) {
            this.categories = Collections.emptyList();
        }


        try {
            this.variant = ProductVariantDTO.parseSingleJson(variantJson);
        } catch (Exception e) {
            this.variant = null;
        }
    }

    public CartItemDTO(Long id, String name) {
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

    public ProductVariantDTO getVariant() {
        return variant;
    }

    public void setVariant(ProductVariantDTO variant) {
        this.variant = variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }
}
