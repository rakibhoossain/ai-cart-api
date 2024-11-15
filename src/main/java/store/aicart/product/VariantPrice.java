package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.aicart.country.Country;
import org.aicart.country.Currency;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity(name = "variant_prices")
public class VariantPrice extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    public Country country;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency currency;

    public BigInteger price;

    public BigInteger discount = BigInteger.ZERO;

    @Column(name = "tax_rate", nullable = false)
    public BigInteger taxRate = BigInteger.ZERO;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    public Boolean isActive = Boolean.FALSE;
}
