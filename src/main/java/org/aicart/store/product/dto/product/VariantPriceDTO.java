package org.aicart.store.product.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;

public class VariantPriceDTO {
    @JsonProperty("country_id")
    private long countryId;
    private BigInteger price;
    private BigInteger comparePrice;
    private BigInteger purchasePrice;

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getComparePrice() {
        return comparePrice;
    }

    public void setComparePrice(BigInteger comparePrice) {
        this.comparePrice = comparePrice;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public BigInteger getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigInteger purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
