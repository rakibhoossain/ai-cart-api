package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.entity.Country;
import org.aicart.entity.Currency;
import org.aicart.entity.WarehouseLocation;
import org.aicart.media.entity.FileStorage;
import org.aicart.media.entity.FileStorageRelation;
import org.aicart.store.product.dto.product.AttributeDTO;
import org.aicart.store.product.dto.product.ProductCreateRequestDTO;
import org.aicart.store.product.dto.product.VariantDTO;
import org.aicart.store.product.entity.*;
import org.aicart.store.user.entity.Shop;

import java.util.*;

@ApplicationScoped
public class ProductCreateService {

    @Transactional
    public Response productCreate(ProductCreateRequestDTO productDTO) {
        Product product = new Product();
        product.name = productDTO.getName();
        product.description = productDTO.getDescription();

        // Handle categories
        if (productDTO.getCategories() != null && !productDTO.getCategories().isEmpty()) {
            List<Integer> categoryIds = productDTO.getCategories();
            List<Category> categories = Category.find("id in ?1", categoryIds).list();
            product.categories = new HashSet<>(categories);
        }

        // Handle images
        if(productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            List<Long> imageIds = productDTO.getImages();
            List<FileStorage> files = FileStorage.find("id in ?1", imageIds).list();

            product.fileRelations = files.stream().map(file -> {
                FileStorageRelation img = new FileStorageRelation();
                img.file = file;
                img.product = product;
                img.score = imageIds.indexOf(file.id);
                return img;
            }).toList();
        }

        product.shop = Shop.findById(1); // Shop
        Country country = Country.findById(1); // Sell location
        Currency currency = Currency.findById(1); // Price currency
        WarehouseLocation warehouseLocation = WarehouseLocation.findById(1); // Warehouse Location


        // Handle variants
        if (productDTO.getVariants() != null) {
            List<ProductVariant> variants = new ArrayList<>();
            for (VariantDTO variantDTO : productDTO.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.product = product;

                // Handle nullable fields with defaults
                variant.sku = variantDTO.getSku() != null ?
                        variantDTO.getSku() + UUID.randomUUID().toString().substring(0, 8) :
                        "variant-" + UUID.randomUUID().toString().substring(0, 8);

                variant.imageId = Long.valueOf(variantDTO.getImageId()); // Can be null


                // Variant attribute values
                if(variantDTO.getAttributes() != null) {
                    List<Integer> attributeValueIds = variantDTO.getAttributes()
                            .stream()
                            .map(AttributeDTO::getAttributeValueId)
                            .toList();

                    List<AttributeValue> attributeValues = AttributeValue.find("id in ?1", attributeValueIds).list();
                    variant.attributeValues = new HashSet<>(attributeValues);
                }


                // Variant prices
                VariantPrice variantPrice = new VariantPrice();
                variantPrice.productVariant = variant;
                variantPrice.country = country;
                variantPrice.price = variantDTO.getPrice();
                variantPrice.currency = currency;
                variantPrice.isActive = true;

                List<VariantPrice> prices = new ArrayList<>();
                prices.add(variantPrice);

                variant.prices = prices;


                // Variant stocks
                VariantStock variantStock = new VariantStock();
                variantStock.productVariant = variant;
                variantStock.quantity = 0;
                variantStock.warehouseLocation = warehouseLocation;
                variant.stock = variantStock;


                variants.add(variant);
            }

            product.variants = variants;
        }

        product.persist();



        return Response.ok(productDTO).build();
    }
}
