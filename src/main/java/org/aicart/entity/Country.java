package org.aicart.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity(name = "countries")
public class Country extends PanacheEntity {

    @Column(length = 4, nullable = false, unique = true)
    public String code; // e.g., "USA", "ES"

    @Column(length = 50, nullable = false)
    public String name; // e.g., "United States", "Spain"

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency currency;
}