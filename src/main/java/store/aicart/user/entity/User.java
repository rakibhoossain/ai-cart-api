package store.aicart.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import store.aicart.order.entity.Cart;
import store.aicart.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User extends PanacheEntity {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "password", nullable = false)
    public String password; // Hashed password for authentication

    @OneToMany(mappedBy = "user")
    public List<Cart> carts;

    @OneToMany(mappedBy = "user")
    public List<Order> orders;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
