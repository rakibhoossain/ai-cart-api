package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.product.ProductCollectionFieldEnum;
import org.aicart.store.product.ProductCollectionOperatorEnum;

@Entity
@Table(name = "product_collection_conditions")
public class ProductCollectionCondition extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id", nullable = false)
    public ProductCollection collection;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "field", nullable = false)
    public ProductCollectionFieldEnum field;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "operator", nullable = false)
    public ProductCollectionOperatorEnum operator;

    @Column(name = "string_value")
    public String stringValue;

    @Column(name = "numeric_value")
    public Integer numericValue;

    @Column(name = "reference_id")
    public Long referenceId;
}
