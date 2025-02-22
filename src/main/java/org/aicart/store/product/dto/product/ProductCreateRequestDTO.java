package org.aicart.store.product.dto.product;

import org.aicart.store.product.ProductStatusEnum;

import java.util.List;

public class ProductCreateRequestDTO {
    private List<Long> images;
    private List<Long> categories;  // Can be null
    private String name;
    private String description;
    private List<VariantDTO> variants;

    private ProductStatusEnum status;
    private String metaTitle;
    private String metaDescription;
    private Long productType;
    private Long productBrand;

    private List<Long> collections;
    private List<Long> tags;
    private List<ProductTaxDTO> taxes;
    private ProductShippingDTO shipping;

    public ProductShippingDTO getShipping() { return shipping; }
    public void setShipping(ProductShippingDTO shipping) { this.shipping = shipping; }

    public List<ProductTaxDTO> getTaxes() { return taxes; }
    public void setTaxes(List<ProductTaxDTO> taxes) { this.taxes = taxes; }

    public List<Long> getCollections() { return collections; }
    public void setCollections(List<Long> collections) { this.collections = collections; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }

    public Long getProductBrand() { return productBrand; }
    public void setProductBrand(Long productBrand) { this.productBrand = productBrand; }

    public Long getProductType() { return productType; }
    public void setProductType(Long productType) { this.productType = productType; }

    public ProductStatusEnum getStatus() { return status; }
    public void setStatus(ProductStatusEnum status) { this.status = status; }

    public List<Long> getTags() { return tags; }
    public void setTags(List<Long> tags) { this.tags = tags; }

    public List<Long> getImages() { return images; }
    public void setImages(List<Long> images) { this.images = images; }

    public List<Long> getCategories() { return categories; }
    public void setCategories(List<Long> categories) { this.categories = categories; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<VariantDTO> getVariants() { return variants; }
    public void setVariants(List<VariantDTO> variants) { this.variants = variants; }
}

