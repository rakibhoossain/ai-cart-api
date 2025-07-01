package org.aicart.store.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.product.entity.ProductVariant;

@Entity
@Table(name = "order_exchange_items")
public class OrderExchangeItem extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    public OrderExchange exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_order_item_id", nullable = false)
    public OrderItem originalOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_variant_id", nullable = false)
    public ProductVariant newVariant;

    @Column(name = "quantity", nullable = false)
    public Integer quantity;

    @Column(name = "reason")
    public String reason;
}
