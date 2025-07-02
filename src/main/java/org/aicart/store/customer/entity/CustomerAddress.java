package org.aicart.store.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_address")
public class CustomerAddress extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    public Customer customer;

    @Column(name = "type", length = 20)
    public String type; // BILLING, SHIPPING, BOTH

    @Column(name = "first_name", nullable = false, length = 30)
    public String firstName;

    @Column(name = "last_name", length = 30)
    public String lastName;

    @Column(name = "company", length = 100)
    public String company;

    @Column(name = "line1", nullable = false)
    public String line1;

    @Column(name = "line2")
    public String line2;

    @Column(name = "city", nullable = false, length = 30)
    public String city;

    @Column(name = "state", nullable = false, length = 30)
    public String state;

    @Column(name = "postal_code", nullable = false, length = 20)
    public String postalCode;

    @Column(name = "country", nullable = false, length = 50)
    public String country;

    @Column(name = "country_code", length = 2)
    public String countryCode; // ISO 3166-1 alpha-2

    @Column(name = "phone", length = 20)
    public String phone;

    @Column(name = "is_default", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean isDefault = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
