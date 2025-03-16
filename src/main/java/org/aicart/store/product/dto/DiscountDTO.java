package org.aicart.store.product.dto;

import jakarta.validation.constraints.*;
import org.aicart.store.product.DiscountAppliesToEnum;
import org.aicart.store.product.DiscountPurchaseType;
import org.aicart.store.product.ProductDiscountAmountTypEnum;
import org.aicart.store.product.ProductDiscountEnum;
import org.aicart.store.product.validation.ValidDiscount;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@ValidDiscount
public class DiscountDTO {
    public Long id;

    @NotNull(message = "Discount type is required")
    public ProductDiscountEnum discountType;

    @NotNull(message = "Discount method is required")
    public Boolean isAutomatic;

    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    public String coupon;

    @Size(max = 100, message = "Title must not exceed 100 characters")
    public String title;

    @NotNull(message = "Start date is required")
    public Instant startAt;

    public Instant endAt;

    @NotNull(message = "Amount type is required")
    public ProductDiscountAmountTypEnum amountType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    public BigInteger amount;

    @NotNull(message = "Active status is required")
    public Boolean isActive;

    @NotNull(message = "Purchase type is required")
    public DiscountPurchaseType purchaseType;

    @NotNull(message = "Applies to field is required")
    public DiscountAppliesToEnum appliesTo;

    @PositiveOrZero(message = "Minimum amount must be 0 or greater")
    public Integer minAmount;

    @PositiveOrZero(message = "Minimum quantity must be 0 or greater")
    public Integer minQuantity;

    public List<String> combinations;

    public List<Long> locations;

    @PositiveOrZero(message = "Max use must be 0 or greater")
    public Integer maxUse;

    @PositiveOrZero(message = "Max customer use must be 0 or greater")
    public Integer maxCustomerUse;

    public List<Long> variantIds;
    public List<Long> collectionIds;
}
