package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.aicart.store.product.ProductCollectionTypeEnum;
import org.aicart.store.product.ProductConditionMatchEnum;
import org.aicart.store.product.validation.ValidCollectionType;

import java.util.List;

@ValidCollectionType
public class ProductCollectionDTO {
    public Long id;

    @NotNull(message = "Title is required")
    @Size(min = 5, message = "Title must be at least 5 characters")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    public String title;

    @NotNull(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    @Size(max = 10000, message = "Description must not exceed 100 characters")
    public String description;

    @NotNull(message = "Collection type is required")
    @JsonProperty("collection_type")
    public ProductCollectionTypeEnum collectionType;

//    @NotNull(message = "Collection condition match type is required")
    @JsonProperty("condition_match")
    public ProductConditionMatchEnum conditionMatch;

    @Valid
    @JsonProperty("conditions")
    public List<ProductCollectionConditionDTO> conditions;

    @NotNull(message = "Active status is required")
    @JsonProperty("is_active")
    public Boolean isActive;

    @NotEmpty(message = "At least one location is required")
    @JsonProperty("locations")
    public List<Long> locations;

    @JsonProperty("product_ids")
    public List<Long> productIds;

    @JsonProperty("file_id")
    public Long fileId;
}
