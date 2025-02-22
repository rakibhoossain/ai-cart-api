package org.aicart.store.product.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductTaxDTO {
    @JsonProperty("country_id")
    private long countryId;

    @JsonProperty("tax_id")
    private long taxId;

    public long getCountryId() { return countryId; }
    public void setCountryId(long countryId) { this.countryId = countryId; }

    public Long getTaxId() { return taxId; }
    public void setTaxId(Long taxId) { this.taxId = taxId; }
}
