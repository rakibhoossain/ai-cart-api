package store.aicart.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import store.aicart.product.Product;
import store.aicart.product.ProductVariant;
import java.math.BigInteger;

@Entity
@Table(name = "order_items")
public class OrderItem extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant variant;

    @Column(name = "quantity", nullable = false)
    public int quantity;

    @Column(name = "price", nullable = false)
    public BigInteger price;

    @Column(name = "discount", nullable = false)
    public BigInteger discount;

    @Column(name = "tax", nullable = false)
    public BigInteger tax;

    @Column(name = "tax_rate", nullable = false)
    public int taxRate;
}
