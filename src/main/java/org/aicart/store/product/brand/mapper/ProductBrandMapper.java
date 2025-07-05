package org.aicart.store.product.brand.mapper;

import org.aicart.store.product.brand.dto.ProductBrandDTO;
import org.aicart.store.product.entity.ProductBrand;

public class ProductBrandMapper {

    public static ProductBrandDTO toDto(ProductBrand brand) {
        if (brand == null) {
            return null;
        }

        return new ProductBrandDTO(
            brand.id,
            brand.name,
            brand.description,
            brand.logo,
            brand.website,
            brand.createdAt,
            brand.updatedAt
        );
    }

    public static ProductBrand toEntity(ProductBrandDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductBrand brand = new ProductBrand();
        brand.name = dto.getName();
        brand.description = dto.getDescription();
        brand.logo = dto.getLogo();
        brand.website = dto.getWebsite();
        return brand;
    }

    public static void updateEntity(ProductBrand brand, ProductBrandDTO dto) {
        if (brand != null && dto != null) {
            brand.name = dto.getName();
            brand.description = dto.getDescription();
            brand.logo = dto.getLogo();
            brand.website = dto.getWebsite();
        }
    }
}