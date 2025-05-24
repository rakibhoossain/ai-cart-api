package org.aicart.store.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.authentication.IdentifiableEntity;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"shop_id", "email"})
        })
public class Customer extends PanacheEntity implements IdentifiableEntity {

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    @Column(name = "email", nullable = false)
    public String email;

    @Column(name = "phone")
    public String phone;

    @Column(name = "language_code", nullable = false, columnDefinition = "VARCHAR(2) DEFAULT 'en'")
    public String languageCode = "en"; // ISO 639-1 Code

    @Column(name = "password")
    public String password; // Hashed password for authentication

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Column(name = "newsletter_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean newsletterSubscribe = false;

    @Column(name = "email_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean emailSubscribe = false;

    @Column(name = "phonel_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean phoneSubscribe = false;

    @Column(name = "last_login_at")
    public LocalDateTime lastLoginAt = null;

    @Enumerated(EnumType.STRING)
    public AccountStatus accountStatus = AccountStatus.ACTIVE;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_address_id")
    public CustomerAddress primaryAddress;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<CustomerAddress> addresses = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String getIdentifier() {
        return "customer";
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING
    }
}
