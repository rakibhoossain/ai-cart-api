package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.dto.ProductCollectionConditionDTO;
import org.aicart.store.product.dto.ProductCollectionDTO;
import org.aicart.store.product.entity.Product;
import org.aicart.store.product.entity.ProductCollection;
import org.aicart.store.product.entity.ProductCollectionCondition;
import org.aicart.store.product.entity.ProductVariant;
import org.aicart.store.user.entity.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProductCollectionService {

    public List<ProductCollection> getAll() {
        return ProductCollection.listAll();
    }

    @Transactional
    public ProductCollection create(ProductCollectionDTO collectionDTO) {
        ProductCollection collection = new ProductCollection();
        collection.title = collectionDTO.title;
        collection.description = collectionDTO.description;
        collection.collectionType = collectionDTO.collectionType;
        collection.shop = Shop.findById(1);

        // Fetch product by IDs and set them
        if (collectionDTO.collectionType == ProductCollectionTypeEnum.MANUAL && collectionDTO.productIds != null && !collectionDTO.productIds.isEmpty()) {
            List<Product> variants = Product.list("id IN ?1", collectionDTO.productIds);
            collection.products = Set.copyOf(variants);
        }

        // Store conditions
        if(collectionDTO.collectionType == ProductCollectionTypeEnum.SMART && collectionDTO.conditions != null && !collectionDTO.conditions.isEmpty()) {
            List<ProductCollectionCondition> newConditions = new ArrayList<>();
            for (ProductCollectionConditionDTO conditionDto : collectionDTO.conditions) {
                ProductCollectionCondition condition = new ProductCollectionCondition();
                condition.field = conditionDto.field;
                condition.operator = conditionDto.operator;

                if (condition.field.equals(ProductCollectionFieldEnum.PRICE)) {
                    if (conditionDto.value instanceof Number) {
                        condition.numericValue = (Integer) conditionDto.value;
                    } else {
                        throw new WebApplicationException(
                                "Value for " + condition.field + " must be numeric",
                                Response.Status.BAD_REQUEST);
                    }
                } else {
                    condition.stringValue = conditionDto.value != null ? conditionDto.value.toString() : null;
                }

                newConditions.add(condition);
            }

            collection.conditions = newConditions;
        }


        collection.persist();

        return collection;
    }
}
