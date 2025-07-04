package org.aicart.store.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "shop_id"}),
    @UniqueConstraint(columnNames = {"slug", "shop_id"})
})
public class CustomerTag extends PanacheEntity {

    @Column(nullable = false, length = 50)
    public String name;

    @Column(nullable = false, length = 50)
    public String slug;

    @Column(length = 7)
    public String color;

    @Column(length = 500)
    public String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @ManyToMany(mappedBy = "tags")
    public Set<Customer> customers = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        customer.tags.add(this);
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        customer.tags.remove(this);
    }
}
