package store.aicart.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import store.aicart.order.dto.OrderBillingDTO;
import store.aicart.order.dto.OrderShippingDTO;
import store.aicart.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    public User user;

    @Column(name = "session_id", nullable = true)
    public String sessionId; // UUID for guest users

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<CartItem> items;

    @Column(name = "billing", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    public OrderBillingDTO billing;

    @Column(name = "shipping", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    public OrderShippingDTO shipping;


    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
