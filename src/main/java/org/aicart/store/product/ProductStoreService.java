package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.entity.Country;
import org.aicart.entity.Currency;
import org.aicart.entity.WarehouseLocation;
import org.aicart.media.FileAssociation;
import org.aicart.media.entity.FileStorage;
import org.aicart.media.entity.FileStorageRelation;
import org.aicart.store.product.dto.product.AttributeDTO;
import org.aicart.store.product.dto.product.ProductCreateRequestDTO;
import org.aicart.store.product.dto.product.VariantDTO;
import org.aicart.store.product.entity.*;
import org.aicart.store.user.entity.Shop;

import java.math.BigInteger;
import java.util.*;

@ApplicationScoped
public class ProductStoreService {

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



        // 6. No need to call persist() explicitly for managed entities
//        return Response.ok(ProductCreateRequestDTO.fromEntity(product)).build();

        product.persist();
//
//
//
        return Response.ok(productDTO).build();
    }


    @Transactional
    public Response productUpdate(BigInteger productId, ProductCreateRequestDTO productDTO) {

        // 1. Find product with null check
        Product product = Product.findById(productId);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // 2. Update basic fields
        product.name = productDTO.getName();
        product.description = productDTO.getDescription();

        // 3. Handle categories with batch query
        if (productDTO.getCategories() != null && !productDTO.getCategories().isEmpty()) {
            List<Category> categories = Category.list("id in ?1", productDTO.getCategories());
            product.categories.clear();
            product.categories.addAll(categories);
        }



        if(productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            FileStorageRelation.delete("associatedType = ?1 AND associatedId = ?2", FileAssociation.PRODUCT.getValue(), productId);

            List<Long> imageIds = productDTO.getImages();
            List<FileStorage> files = FileStorage.find("id in ?1", imageIds).list();

            List<FileStorageRelation> fileRelations = files.stream().map(file -> {
                FileStorageRelation img = new FileStorageRelation();
                img.file = file;
                img.product = product;
                img.associatedId = product.id;
                img.associatedType = FileAssociation.PRODUCT.getValue();
                img.score = imageIds.indexOf(file.id);
                return img;
            }).toList();

            product.fileRelations.addAll(fileRelations);
        }

        // 4. Handle images with proper relationship management
//        if (productDTO.getImages() != null) {
//            List<Long> imageIds = productDTO.getImages();
//
//            // Delete existing relations first
//            FileStorageRelation.delete("associatedType = ?1 AND associatedId = ?2", FileAssociation.PRODUCT.getValue(), productId);
//
//            List<FileStorage> files = FileStorage.find("id in ?1", imageIds).list();
//
////            List<FileStorageRelation> fileRelations = new ArrayList<>();
//            for (FileStorage file : files) {
//                FileStorageRelation img = new FileStorageRelation();
//                img.file = file;
//                img.product = product;
//                img.associatedId = product.id;
//                img.associatedType = FileAssociation.PRODUCT.getValue();
//                img.score = imageIds.indexOf(file.id);
//                img.persist();
////                fileRelations.add(img);
//            }
//        }

        product.shop = Shop.findById(1); // Shop
        Country country = Country.findById(1); // Sell location
        Currency currency = Currency.findById(1); // Price currency
        WarehouseLocation warehouseLocation = WarehouseLocation.findById(1); // Warehouse Location


        // 5. Handle variants with proper update logic
        if (productDTO.getVariants() != null) {
            List<ProductVariant> updatedVariants = new ArrayList<>();

            for (VariantDTO variantDTO : productDTO.getVariants()) {
                ProductVariant variant = handleVariantUpdate(product, variantDTO);
                updatedVariants.add(variant);
            }

            // Remove deleted variants
            List<Integer> incomingIds = productDTO.getVariants().stream()
                    .map(VariantDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();

//            if (!incomingIds.isEmpty()) {
//                ProductVariant.delete("product.id = ?1 and id not in ?2", productId, incomingIds);
//            }

            product.variants = updatedVariants;
        }

        product.persist();

        return Response.ok(productDTO).build();
    }



    private ProductVariant handleVariantUpdate(Product product, VariantDTO variantDTO) {

        ProductVariant variant = variantDTO.getId() != null ? ProductVariant.findById(variantDTO.getId()) : new ProductVariant();
        if(variant == null) {
            variant = new ProductVariant();
        }

        variant.product = product;

        // 7. Proper SKU handling
        variant.sku = Optional.ofNullable(variantDTO.getSku())
                .orElse("variant-" + UUID.randomUUID().toString().substring(0, 8));

        // 8. Handle attributes with batch query
        if (variantDTO.getAttributes() != null) {
            List<Integer> attributeValueIds = variantDTO.getAttributes().stream()
                    .map(AttributeDTO::getAttributeValueId)
                    .toList();;

            List<AttributeValue> attributeValues = AttributeValue.list("id in ?1", attributeValueIds);
            variant.attributeValues.clear();
            variant.attributeValues.addAll(attributeValues);
        }

        // 9. Handle prices
        if (variantDTO.getPrice() != null) {
            VariantPrice price = Optional.ofNullable(variant.prices)
                    .flatMap(p -> p.stream().findFirst())
                    .orElseGet(VariantPrice::new);

            price.price = variantDTO.getPrice();
            price.isActive = true;
            variant.prices = List.of(price);
        }

        // 10. Handle stock with merge logic
        if (variant.stock == null) {
            variant.stock = new VariantStock();
        }
//        variant.stock.quantity = variantDTO.getStockQuantity() != null ?
//                variantDTO.getStockQuantity() :
//                variant.stock.quantity;

        return variant;
    }
}
