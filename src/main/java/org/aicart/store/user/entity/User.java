package org.aicart.store.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.authentication.IdentifiableEntity;
import org.aicart.store.order.entity.Cart;
import org.aicart.store.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User extends PanacheEntity implements IdentifiableEntity {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "password")
    public String password; // Hashed password for authentication

    @OneToMany(mappedBy = "user")
    public List<Cart> carts;

    @OneToMany(mappedBy = "user")
    public List<Order> orders;

    @Column(name = "last_login_at")
    public LocalDateTime lastLoginAt = null;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "verified_at")
    public long verifiedAt = 0;

    @Override
    public String getIdentifier() {
        return "user";
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}
