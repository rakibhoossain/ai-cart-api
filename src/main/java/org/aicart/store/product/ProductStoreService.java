package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.entity.Country;
import org.aicart.entity.Currency;
import org.aicart.entity.WarehouseLocation;
import org.aicart.media.FileAssociation;
import org.aicart.media.entity.FileStorage;
import org.aicart.media.entity.FileStorageRelation;
import org.aicart.store.product.dto.product.*;
import org.aicart.store.product.entity.*;
import org.aicart.store.user.entity.Shop;

import java.math.BigInteger;
import java.util.*;

@ApplicationScoped
public class ProductStoreService {

    @PersistenceContext
    EntityManager entityManager;

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
                if(variantDTO.getPrices() != null && !variantDTO.getPrices().isEmpty()) {
                    List<VariantPrice> prices = new ArrayList<>();

                    for (VariantPriceDTO variantPriceDTO : variantDTO.getPrices()) {
                        VariantPrice variantPrice = new VariantPrice();
                        variantPrice.productVariant = variant;
                        variantPrice.country = Country.findById(variantPriceDTO.getCountryId());
                        variantPrice.price = variantPriceDTO.getPrice();
                        variantPrice.currency = currency;
                        variantPrice.isActive = true;

                        prices.add(variantPrice);
                    }
                    variant.prices = prices;
                }

                // Variant stocks
                if(variantDTO.getStocks() != null && !variantDTO.getStocks().isEmpty()) {
                    List<VariantStock> variantStocks = new ArrayList<>();

                    for (VariantStockDTO variantStockDTO : variantDTO.getStocks()) {
                        VariantStock variantStock = new VariantStock();
                        variantStock.warehouseLocation = WarehouseLocation.findById(variantStockDTO.getWarehouseId());
                        variantStock.productVariant = variant;
                        variantStock.quantity = variantStockDTO.getQuantity();

                        System.out.println("variantStockDTO.getWarehouseId() : " + variantStockDTO.getWarehouseId());

                        variantStocks.add(variantStock);
                    }

                    variant.stocks = variantStocks;
                }

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

        // Existing variants ID
        List<Long> existingVariantsId = product.variants.stream()
                .map(v -> v.id)
                .filter(Objects::nonNull)
                .toList();



        // 2. Update basic fields
        product.name = productDTO.getName();
        product.description = productDTO.getDescription();

        // 3. Handle categories with batch query
        if (productDTO.getCategories() != null && !productDTO.getCategories().isEmpty()) {
            List<Category> categories = Category.list("id in ?1", productDTO.getCategories());

            // Clear categories
            entityManager.createNativeQuery("DELETE FROM product_category WHERE product_id = ?1")
                    .setParameter(1, product.id)
                    .executeUpdate();

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
            List<Long> incomingIds = productDTO.getVariants().stream()
                    .map(VariantDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();

            if (!incomingIds.isEmpty()) {
                List<Long> filteredList = existingVariantsId.stream()
                        .filter(id -> !incomingIds.contains(id))
                        .toList();

                // Remove garbage
                if(!filteredList.isEmpty()) {
                    entityManager.createNativeQuery("DELETE FROM product_variant_value WHERE variant_id IN (?1)")
                    .setParameter(1, filteredList)
                    .executeUpdate();

                    VariantStock.delete("productVariant.id in ?1", filteredList);
                    VariantPrice.delete("productVariant.id in ?1", filteredList);
                    ProductVariant.delete("product.id = ?1 and id in ?2", productId, filteredList);
                }
            }

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
        variant.sku = variantDTO.getSku();

        // 8. Handle attributes with batch query
        if (variantDTO.getAttributes() != null) {
            List<Integer> attributeValueIds = variantDTO.getAttributes().stream()
                    .map(AttributeDTO::getAttributeValueId)
                    .toList();;

            List<AttributeValue> attributeValues = AttributeValue.list("id in ?1", attributeValueIds);

            // Clear attributes
            if(variant.id != null) {
                entityManager.createNativeQuery("DELETE FROM product_variant_value WHERE variant_id = ?1")
                        .setParameter(1, variant.id)
                        .executeUpdate();
            }

            variant.attributeValues = new HashSet<>(attributeValues);
        }

        // 9. Handle prices
        if(variantDTO.getPrices() != null && !variantDTO.getPrices().isEmpty()) {

            VariantPrice.delete("productVariant.id in ?1", variant.id);

            List<VariantPrice> prices = new ArrayList<>();

            for (VariantPriceDTO variantPriceDTO : variantDTO.getPrices()) {
                VariantPrice variantPrice = new VariantPrice();
                variantPrice.productVariant = variant;
                variantPrice.country = Country.findById(variantPriceDTO.getCountryId());
                variantPrice.price = variantPriceDTO.getPrice();
                variantPrice.currency = Currency.findById(1);
                variantPrice.isActive = true;

                prices.add(variantPrice);
            }
            variant.prices = prices;
        }

        // 10. Handle stock with merge logic
        if(variantDTO.getStocks() != null && !variantDTO.getStocks().isEmpty()) {
            VariantStock.delete("productVariant.id in ?1", variant.id);

            List<VariantStock> variantStocks = new ArrayList<>();

            for (VariantStockDTO variantStockDTO : variantDTO.getStocks()) {
                VariantStock variantStock = new VariantStock();
                variantStock.warehouseLocation = WarehouseLocation.findById(variantStockDTO.getWarehouseId());
                variantStock.productVariant = variant;
                variantStock.quantity = variantStockDTO.getQuantity();

                variantStocks.add(variantStock);
            }

            variant.stocks = variantStocks;
        }

//        // 9. Handle prices
//        if (variantDTO.getPrice() != null) {
//            VariantPrice price = Optional.ofNullable(variant.prices)
//                    .flatMap(p -> p.stream().findFirst())
//                    .orElseGet(VariantPrice::new);
//
//            price.price = variantDTO.getPrice();
//            price.isActive = true;
//            variant.prices = List.of(price);
//        }
//
//        // 10. Handle stock with merge logic
//        if (variant.stock == null) {
//            variant.stock = new VariantStock();
//        }









//        variant.stock.quantity = variantDTO.getStockQuantity() != null ?
//                variantDTO.getStockQuantity() :
//                variant.stock.quantity;

        return variant;
    }
}
