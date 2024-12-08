package store.aicart.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_shipping")
public class OrderShipping extends PanacheEntity {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @Column(name = "full_name", nullable = false)
    public String fullName;

    @Column(name = "line1", nullable = false)
    public String line1;

    @Column(name = "line2")
    public String line2;

    @Column(name = "city", nullable = false)
    public String city;

    @Column(name = "state", nullable = false)
    public String state;

    @Column(name = "postal_code", nullable = false)
    public String postalCode;

    @Column(name = "country", nullable = false)
    public String country; // ISO 3166-1 alpha-2

    @Column(name = "phone", nullable = true)
    public String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

