package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.aicart.store.product.*;
import org.aicart.store.product.validation.ValidDiscount;

import java.math.BigInteger;
import java.util.List;

@ValidDiscount
public class DiscountDTO {
    public Long id;

    @NotNull(message = "Discount type is required")
    @JsonProperty("discount_type")
    public ProductDiscountEnum discountType;

    @NotNull(message = "Discount method is required")
    @JsonProperty("is_automatic")
    public Boolean isAutomatic;

    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    public String coupon;

    @Size(max = 100, message = "Title must not exceed 100 characters")
    public String title;

    @NotNull(message = "Start date is required")
    @JsonProperty("start_at")
    public BigInteger startAt;

    @JsonProperty("end_at")
    public BigInteger endAt;

    @NotNull(message = "Amount type is required")
    @JsonProperty("amount_type")
    public ProductDiscountAmountTypEnum amountType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    public BigInteger amount;

    @NotNull(message = "Active status is required")
    @JsonProperty("is_active")
    public Boolean isActive;

    @NotNull(message = "Purchase type is required")
    @JsonProperty("purchase_type")
    public DiscountPurchaseType purchaseType;

    @NotNull(message = "Applies to field is required")
    @JsonProperty("applies_to")
    public DiscountAppliesToEnum appliesTo;

    @PositiveOrZero(message = "Minimum amount must be 0 or greater")
    @JsonProperty("min_amount")
    public Integer minAmount;

    @PositiveOrZero(message = "Minimum quantity must be 0 or greater")
    @JsonProperty("min_quantity")
    public Integer minQuantity;

    public List<CombinationEnum> combinations;

    public List<Long> locations;

    @PositiveOrZero(message = "Max use must be 0 or greater")
    @JsonProperty("max_use")
    public Integer maxUse;

    @PositiveOrZero(message = "Max customer use must be 0 or greater")
    @JsonProperty("max_customer_use")
    public Integer maxCustomerUse;

    @JsonProperty("variant_ids")
    public List<Long> variantIds;

    @JsonProperty("collection_ids")
    public List<Long> collectionIds;

    @JsonProperty("customer_ids")
    public List<Long> customerIds;
}
