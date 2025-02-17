package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Country;
import org.aicart.entity.Currency;

import java.math.BigInteger;

@Entity(name = "variant_prices")
public class VariantPrice extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    public Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    public BigInteger price;

    @Column(name = "purchase_price")
    public BigInteger purchasePrice = BigInteger.ZERO;

    @Column(name = "compare_price")
    public BigInteger comparePrice = BigInteger.ZERO;

    public BigInteger discount = BigInteger.ZERO;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    public Boolean isActive = Boolean.FALSE;
}
