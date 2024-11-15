package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity(name = "prices")
public class Price extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    public BigInteger price;

    @Column(length = 10, nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'USD'")
    public String currency = "USD";

    @Column(name = "effective_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public LocalDateTime effectiveDate = LocalDateTime.now();

    public LocalDateTime endDate;
}
