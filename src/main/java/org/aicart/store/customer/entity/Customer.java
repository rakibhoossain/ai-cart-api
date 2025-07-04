package org.aicart.store.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.authentication.IdentifiableEntity;
import org.aicart.store.order.entity.Cart;
import org.aicart.store.order.entity.Order;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "date_of_birth")
    public LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    public Gender gender;

    @Column(name = "company")
    public String company;

    @Column(name = "job_title")
    public String jobTitle;

    @Column(name = "language_code", nullable = false, columnDefinition = "VARCHAR(2) DEFAULT 'en'")
    public String languageCode = "en"; // ISO 639-1 Code

    @Column(name = "currency_code", nullable = false, columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    public String currencyCode = "USD"; // ISO 4217 Code

    @Column(name = "timezone", columnDefinition = "VARCHAR(50) DEFAULT 'UTC'")
    public String timezone = "UTC";

    @Column(name = "password")
    public String password; // Hashed password for authentication

    @Column(name = "avatar_url")
    public String avatarUrl;

    // Verification status
    @Column(name = "email_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean emailVerified = false;

    @Column(name = "phone_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean phoneVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @OneToMany(mappedBy = "customer")
    public List<Cart> carts;

    @OneToMany(mappedBy = "customer")
    public List<Order> orders;

    // Marketing preferences
    @Column(name = "newsletter_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean newsletterSubscribe = false;

    @Column(name = "email_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean emailSubscribe = false;

    @Column(name = "phone_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean phoneSubscribe = false;

    @Column(name = "sms_subscribe", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean smsSubscribe = false;

    // Customer metrics
    @Column(name = "total_orders", columnDefinition = "INTEGER DEFAULT 0")
    public Integer totalOrders = 0;

    @Column(name = "total_spent", columnDefinition = "BIGINT DEFAULT 0")
    public Long totalSpent = 0L; // In cents

    @Column(name = "average_order_value", columnDefinition = "BIGINT DEFAULT 0")
    public Long averageOrderValue = 0L; // In cents

    @Column(name = "lifetime_value", columnDefinition = "BIGINT DEFAULT 0")
    public Long lifetimeValue = 0L; // In cents

    // Customer behavior
    @Column(name = "first_order_at")
    public LocalDateTime firstOrderAt;

    @Column(name = "last_order_at")
    public LocalDateTime lastOrderAt;

    @Column(name = "last_activity_at")
    public LocalDateTime lastActivityAt;

    @Column(name = "last_login_at")
    public LocalDateTime lastLoginAt = null;

    @Enumerated(EnumType.STRING)
    public AccountStatus accountStatus = AccountStatus.ACTIVE;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_address_id")
    public CustomerAddress primaryAddress;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<CustomerAddress> addresses = new ArrayList<>();

    // Customer segmentation
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", columnDefinition = "VARCHAR(20) DEFAULT 'REGULAR'")
    public CustomerType customerType = CustomerType.REGULAR;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_tier", columnDefinition = "VARCHAR(20) DEFAULT 'BRONZE'")
    public CustomerTier customerTier = CustomerTier.BRONZE;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "customer_tag_pivot",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    public Set<CustomerTag> tags = new HashSet<>();

    @Column(name = "legacy_tags", columnDefinition = "TEXT")
    public String legacyTags; // Keep for migration purposes

    // Tier management fields
    @Column(name = "tier_updated_at")
    public LocalDateTime tierUpdatedAt;

    @Column(name = "tier_overridden")
    public Boolean tierOverridden = false;

    @Column(name = "tier_override_reason", length = 500)
    public String tierOverrideReason;

    // Customer type management
    @Column(name = "customer_type_overridden")
    public Boolean customerTypeOverridden = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    public String notes; // Admin notes about the customer

    // Account status
    @Column(name = "account_locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean accountLocked = false;

    @Column(name = "account_locked_reason")
    public String accountLockedReason;

    @Column(name = "account_locked_at")
    public LocalDateTime accountLockedAt;

    // Tax information
    @Column(name = "tax_exempt", columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean taxExempt = false;

    @Column(name = "tax_exemption_reason")
    public String taxExemptionReason;

    @Column(name = "vat_number")
    public String vatNumber;

    @Column(name = "tax_id")
    public String taxId;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "verified_at")
    public Long verifiedAt = 0L;

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

    // Helper methods for tag management
    public void addTag(CustomerTag tag) {
        tags.add(tag);
        tag.customers.add(this);
    }

    public void removeTag(CustomerTag tag) {
        tags.remove(tag);
        tag.customers.remove(this);
    }

    public void clearTags() {
        for (CustomerTag tag : new HashSet<>(tags)) {
            removeTag(tag);
        }
    }
}
