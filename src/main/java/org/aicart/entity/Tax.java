package org.aicart.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

@Entity(name = "taxes")
public class Tax extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    public Country country;

    @Column(name = "name", nullable = false, length = 50)
    public String name; // E.g. Full Tax, Reduced Tax

    @Column(name = "tax_rate", nullable = false)
    public Integer taxRate; // E.g., 1900 means 19.00%

    @Column(name = "description", nullable = true, length = 100)
    public String description; // Standard VAT rate in Germany

    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    public Boolean isDefault;
}