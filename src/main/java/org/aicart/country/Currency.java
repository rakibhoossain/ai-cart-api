package org.aicart.country;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "currencies")
public class Currency extends PanacheEntity {
    @Column(length = 4, nullable = false, unique = true)
    public String code; // e.g., USD, EUR

    @Column(length = 20, nullable = false)
    public String name; // e.g., US Dollar, Euro
}
