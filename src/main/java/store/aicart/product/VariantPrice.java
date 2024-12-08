package store.aicart.product;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency currency;

    public BigInteger price;

    public BigInteger discount = BigInteger.ZERO;

    @Column(name = "tax_rate", nullable = false)
    public BigInteger taxRate = BigInteger.ZERO;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    public Boolean isActive = Boolean.FALSE;
}
