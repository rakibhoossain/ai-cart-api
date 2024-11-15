package org.aicart.country;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "countries")
public class Country extends PanacheEntity {

    @Column(length = 4, nullable = false, unique = true)
    public String code; // e.g., "usa", "es"

    @Column(length = 50, nullable = false)
    public String name; // e.g., "United States", "Spain"

}