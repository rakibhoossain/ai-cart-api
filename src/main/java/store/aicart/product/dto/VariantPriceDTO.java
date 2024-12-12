package store.aicart.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VariantPriceDTO {
    private Integer price;
    private Integer discount;

    @JsonProperty("tax_rate")
    private Integer taxRate;

    @JsonProperty("currency_id")
    private Integer currencyId;

    @JsonProperty("discount_end_at")
    private Long discountEndAt;

    @JsonProperty("discount_type")
    private String discountType;

    // Getters and Setters
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
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
}

