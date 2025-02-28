package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.entity.Country;
import org.aicart.entity.Tax;
import org.aicart.entity.WarehouseLocation;
import org.aicart.media.FileAssociation;
import org.aicart.media.entity.FileStorage;
import org.aicart.media.entity.FileStorageRelation;
import org.aicart.store.product.entity.ProductTaxRate;
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

        product.metaTitle = productDTO.getMetaTitle();
        product.metaDescription = productDTO.getMetaDescription();
        product.status = productDTO.getStatus();

        if(productDTO.getProductType() != null) {
            product.productType = ProductType.findById(productDTO.getProductType());
        } else {
            product.productType = null;
        }

        if(productDTO.getProductBrand() != null) {
            product.productBrand = ProductBrand.findById(productDTO.getProductBrand());
        } else {
            product.productBrand = null;
        }

        // Handle Shipping
        if (productDTO.getShipping() != null) {
            ProductShippingDTO shippingDTO = productDTO.getShipping();

            ProductShipping productShipping = new ProductShipping();
            productShipping.weight = shippingDTO.getWeight();
            productShipping.weightUnit = shippingDTO.getWeightUnit();
            productShipping.product = product; // Assign persisted Product
            product.productShipping = productShipping;
        }

        // Handle Collection
        if (productDTO.getCollections() != null && !productDTO.getCollections().isEmpty()) {
            List<Long> collectionIds = productDTO.getCollections();
            List<ProductCollection> collections = ProductCollection.find("id in ?1", collectionIds).list();
            product.collections = new HashSet<>(collections);
        }

        // Handle Tags
        if (productDTO.getTags() != null && !productDTO.getTags().isEmpty()) {
            List<Long> tagIds = productDTO.getTags();
            List<ProductTag> tags = ProductTag.find("id in ?1", tagIds).list();
            product.tags = new HashSet<>(tags);
        }

        // Handle Taxes
        if (productDTO.getTaxes() != null && !productDTO.getTaxes().isEmpty()) {

            List<ProductTaxRate> taxes = new ArrayList<>();

            for (ProductTaxDTO productTaxDTO : productDTO.getTaxes()) {
                ProductTaxRate productTaxRate = new ProductTaxRate();
                productTaxRate.country = Country.findById(productTaxDTO.getCountryId());
                productTaxRate.tax = Tax.findById(productTaxDTO.getTaxId());
                if(productTaxRate.tax != null) {
                    taxes.add(productTaxRate);
                }
            }
            product.taxes = taxes;
        }

        // Handle categories
        if (productDTO.getCategories() != null && !productDTO.getCategories().isEmpty()) {
            List<Long> categoryIds = productDTO.getCategories();
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

        product.shop = Shop.findById(1); // TODO:: Shop


        // Handle variants
        if (productDTO.getVariants() != null) {
            List<ProductVariant> variants = new ArrayList<>();
            boolean accept = true;
            for (VariantDTO variantDTO : productDTO.getVariants()) {

                if(!accept) continue;

                ProductVariant variant = new ProductVariant();
                variant.product = product;

                // Handle nullable fields with defaults
                variant.sku = variantDTO.getSku() != null ?
                        variantDTO.getSku() + UUID.randomUUID().toString().substring(0, 8) :
                        "variant-" + UUID.randomUUID().toString().substring(0, 8);


                if(variantDTO.getImageId() != null) {
                    variant.imageId = Long.valueOf(variantDTO.getImageId());
                }

                // Variant attribute values
                if(variantDTO.getAttributes() != null) {
                    List<Integer> attributeValueIds = variantDTO.getAttributes()
                            .stream()
                            .map(AttributeDTO::getAttributeValueId)
                            .toList();

                    List<AttributeValue> attributeValues = AttributeValue.find("id in ?1", attributeValueIds).list();
                    variant.attributeValues = new HashSet<>(attributeValues);
                } else {
                    accept = false;
                }

                // Variant prices
                if(variantDTO.getPrices() != null && !variantDTO.getPrices().isEmpty()) {
                    List<VariantPrice> prices = new ArrayList<>();

                    for (VariantPriceDTO variantPriceDTO : variantDTO.getPrices()) {
                        VariantPrice variantPrice = new VariantPrice();
                        variantPrice.productVariant = variant;
                        variantPrice.country = Country.findById(variantPriceDTO.getCountryId());
                        variantPrice.price = variantPriceDTO.getPrice();
                        variantPrice.purchasePrice = variantPriceDTO.getPurchasePrice();
                        variantPrice.comparePrice = variantPriceDTO.getComparePrice();
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

        product.persist();
        return Response.ok(productDTO).build();
    }


    @Transactional
    public Response productUpdate(BigInteger productId, ProductCreateRequestDTO productDTO) {

        // 1. Find product with null check
        Product product = Product.findById(productId);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(productDTO.getProductType() != null) {
            product.productType = ProductType.findById(productDTO.getProductType());
        } else {
            product.productType = null;
        }

        if(productDTO.getProductBrand() != null) {
            product.productBrand = ProductBrand.findById(productDTO.getProductBrand());
        } else {
            product.productBrand = null;
        }

        // Existing variants ID
        List<Long> existingVariantsId = product.variants.stream()
                .map(v -> v.id)
                .filter(Objects::nonNull)
                .toList();


        // 2. Update basic fields
        product.name = productDTO.getName();
        product.description = productDTO.getDescription();
        product.metaTitle = productDTO.getMetaTitle();
        product.metaDescription = productDTO.getMetaDescription();
        product.status = productDTO.getStatus();

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

        // Handle Shipping
        if (productDTO.getShipping() != null) {
            ProductShippingDTO shippingDTO = productDTO.getShipping();

            ProductShipping productShipping = ProductShipping.find("product.id = ?1", product.id).firstResult();
            if (productShipping == null) {
                productShipping = new ProductShipping();
            }
            productShipping.weight = shippingDTO.getWeight();
            productShipping.weightUnit = shippingDTO.getWeightUnit();
            productShipping.product = product; // Assign persisted Product
            product.productShipping = productShipping;
        }

        // Handle Collection
        if (productDTO.getCollections() != null && !productDTO.getCollections().isEmpty()) {
            // Clear collections
            entityManager.createNativeQuery("DELETE FROM product_collection_pivot WHERE product_id = ?1")
                    .setParameter(1, product.id)
                    .executeUpdate();

            List<Long> collectionIds = productDTO.getCollections();

            List<ProductCollection> collections = ProductCollection.find("id in ?1", collectionIds).list();
            product.collections = new HashSet<>(collections);
        }

        // Handle Tags
        if (productDTO.getTags() != null && !productDTO.getTags().isEmpty()) {
            // Clear tags
            entityManager.createNativeQuery("DELETE FROM product_tag_pivot WHERE product_id = ?1")
                    .setParameter(1, product.id)
                    .executeUpdate();

            List<Long> tagIds = productDTO.getTags();
            List<ProductTag> tags = ProductTag.find("id in ?1", tagIds).list();
            product.tags = new HashSet<>(tags);
        }

        // Handle Taxes
        ProductTaxRate.delete("product.id = ?1", product.id);
        if (productDTO.getTaxes() != null && !productDTO.getTaxes().isEmpty()) {
            List<ProductTaxRate> taxes = new ArrayList<>();

            for (ProductTaxDTO productTaxDTO : productDTO.getTaxes()) {
                ProductTaxRate productTaxRate = new ProductTaxRate();
                productTaxRate.country = Country.findById(productTaxDTO.getCountryId());

                productTaxRate.tax = Tax.findById(productTaxDTO.getTaxId());
                if(productTaxRate.tax != null) {
                    productTaxRate.product = product;
                    taxes.add(productTaxRate);
                }
            }

            product.taxes = taxes;
        }

        // 5. Handle variants with proper update logic
        if (productDTO.getVariants() != null) {

            List<ProductVariant> updatedVariants = new ArrayList<>();
            boolean accept = true;

            for (VariantDTO variantDTO : productDTO.getVariants()) {

                if(!accept) continue;

                if(variantDTO.getAttributes() == null || variantDTO.getAttributes().isEmpty()) {
                    accept = false;
                }
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

        if(variantDTO.getImageId() != null) {
            variant.imageId = Long.valueOf(variantDTO.getImageId());
        } else {
            variant.imageId = null;
        }

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
                variantPrice.purchasePrice = variantPriceDTO.getPurchasePrice();
                variantPrice.comparePrice = variantPriceDTO.getComparePrice();
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

        return variant;
    }
}
