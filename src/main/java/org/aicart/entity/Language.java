package org.aicart.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "languages")
public class Language extends PanacheEntity {

    @Column(length = 4, nullable = false, unique = true)
    public String code; // e.g., "en", "es"

    @Column(length = 50, nullable = false)
    public String name; // e.g., "English", "Spanish"

}