package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VariantPriceDTO {
    private Integer price;

    @JsonProperty("compare_price")
    private Integer comparePrice;

    private Integer discount;

    @JsonProperty("tax_rate")
    private Integer taxRate;

    @JsonProperty("currency_id")
    private Integer currencyId;

    @JsonProperty("discount_end_at")
    private Long discountEndAt;

    @JsonProperty("discount_type")
    private String discountType; // Discount type: "PERCENTAGE" or "FIXED"

    // Getters and Setters
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     * Calculates the discount based on discountType (either "PERCENTAGE" or "FIXED").
     *
     * @return discount amount based on the discount type
     */
    public Integer getDiscount() {

        if (discount == null || price == null || discountType == null) {
            return 0;  // No discount if the discount or price is null
        }

        return discount;

//        if ("PERCENTAGE".equalsIgnoreCase(discountType)) {
//            // Percentage discount calculation
//            return price * discount / 100;  // Calculate discount as percentage of price
//        } else if ("FIXED".equalsIgnoreCase(discountType)) {
//            // Fixed discount amount
//            return discount;  // Return fixed amount as discount
//        }
//
//        return 0;  // Default return if discount type is unknown
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Integer taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    /**
     * This method calculates the final price after discount.
     * If the discount is not null, it subtracts the discount from the price.
     * Otherwise, it returns the original price.
     *
     * @return final price after applying discount
     */
    @JsonProperty("sell_price")
    public Integer getSellPrice() {
        Integer discount = getDiscount();
        // Calculate the final price after discount
        if (price != null && discount != null && discount > 0) {
            return price - discount;  // If both price and discount exist, return price - discount
        }
        return price;  // If no discount is provided, return the original price
    }


    /**
     * Calculates the discount based on discountType (either "PERCENTAGE" or "FIXED").
     *
     * @return discount amount based on the discount type
     */
    @JsonProperty("discount_percentage")
    public Integer getDiscountPercentage() {

        if (discount != null && "PERCENTAGE".equalsIgnoreCase(discountType)) {
            return discount;
        }

        return null;
    }

    public Long getDiscountEndAt() {
        return discountEndAt;
    }

    public void setDiscountEndAt(Long discountEndAt) {
        this.discountEndAt = discountEndAt;
    }


    public Integer getComparePrice() {
        return comparePrice;
    }

    public void setComparePrice(Integer comparePrice) {
        this.comparePrice = comparePrice;
    }
}

