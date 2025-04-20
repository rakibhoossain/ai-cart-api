package org.aicart.store.product.mapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.ProductCollectionFieldEnum;
import org.aicart.store.product.ProductCollectionTypeEnum;
import org.aicart.store.product.dto.ProductCollectionConditionDTO;
import org.aicart.store.product.dto.ProductCollectionDTO;
import org.aicart.store.product.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.quarkus.hibernate.orm.panache.Panache.getEntityManager;

public class ProductCollectionMapper {

    public static ProductCollectionDTO toDTO(ProductCollection collection) {
        ProductCollectionDTO dto = new ProductCollectionDTO();
        dto.id = collection.id;
        dto.name = collection.name;
        dto.collectionType = collection.collectionType;
        dto.conditionMatch = collection.conditionMatch;
        dto.isActive = collection.isActive;
        return dto;
    }

    public static List<ProductCollectionDTO> toDTOList(List<ProductCollection> collections) {
        return collections.stream().map(ProductCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public static ProductCollection toEntity(ProductCollectionDTO dto) {
        ProductCollection collection = new ProductCollection();
        collection.name = dto.name;
        collection.description = dto.description;
        collection.collectionType = dto.collectionType;

        // Fetch product by IDs and set them
        if (dto.collectionType == ProductCollectionTypeEnum.MANUAL && dto.productIds != null && !dto.productIds.isEmpty()) {
            List<Product> products = Product.list("id IN ?1", dto.productIds);
            collection.products = Set.copyOf(products);
        }

        // Store conditions
        if(dto.collectionType == ProductCollectionTypeEnum.SMART && dto.conditions != null && !dto.conditions.isEmpty()) {
            List<ProductCollectionCondition> newConditions = newConditionValue(collection, dto);
            collection.conditions = Set.copyOf(newConditions);
        }

        return collection;
    }


    public static ProductCollection toUpdate(ProductCollection collection, ProductCollectionDTO dto) {

        collection.name = dto.name;
        collection.description = dto.description;
        collection.collectionType = dto.collectionType;

        // Clear old data
        getEntityManager().createNativeQuery(
                        "DELETE FROM product_collection_pivot WHERE collection_id = ?1; " +
                                "DELETE FROM product_collection_conditions WHERE collection_id = ?1")
                .setParameter(1, collection.id)
                .executeUpdate();


        // Fetch product by IDs and set them
        if (dto.collectionType == ProductCollectionTypeEnum.MANUAL && dto.productIds != null && !dto.productIds.isEmpty()) {
            List<Product> products = Product.list("id IN ?1", dto.productIds);

            System.out.println(products.getFirst());
            collection.products = Set.copyOf(products);
        }

        // Store conditions
        if(dto.collectionType == ProductCollectionTypeEnum.SMART && dto.conditions != null && !dto.conditions.isEmpty()) {
            List<ProductCollectionCondition> newConditions = newConditionValue(collection, dto);
            collection.conditions = Set.copyOf(newConditions);
        }

        return collection;
    }



    private static List<ProductCollectionCondition> newConditionValue(ProductCollection collection, ProductCollectionDTO dto) {
        List<ProductCollectionCondition> newConditions = new ArrayList<>();
        for (ProductCollectionConditionDTO conditionDto : dto.conditions) {
            ProductCollectionCondition condition = new ProductCollectionCondition();
            condition.field = conditionDto.field;
            condition.operator = conditionDto.operator;
            condition.collection = collection;

            if (condition.field.equals(ProductCollectionFieldEnum.PRICE)) {
                if (conditionDto.value instanceof Number) {
                    condition.numericValue = (Integer) conditionDto.value;
                } else {
                    throw new WebApplicationException(
                            "Value for " + condition.field + " must be numeric",
                            Response.Status.BAD_REQUEST);
                }
            } else if(condition.field.equals(ProductCollectionFieldEnum.TAG) || condition.field.equals(ProductCollectionFieldEnum.CATEGORY)) {
                if (conditionDto.value instanceof Number) {
                    condition.referenceId = (Long) conditionDto.value;
                } else {
                    throw new WebApplicationException(
                            "Value for " + condition.field + " must be int",
                            Response.Status.BAD_REQUEST);
                }
            } else {
                condition.stringValue = conditionDto.value != null ? conditionDto.value.toString() : null;
            }
            newConditions.add(condition);
        }

        return newConditions;
    }
}
