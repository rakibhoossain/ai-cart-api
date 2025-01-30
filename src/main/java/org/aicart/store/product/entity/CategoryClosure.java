package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(
        name = "category_closure",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ancestor_id", "descendant_id"})
)
public class CategoryClosure extends PanacheEntityBase {

    @EmbeddedId
    public CategoryClosureId id;

    @Column(nullable = false)
    public int depth;

    // Relationships
    @MapsId("ancestorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_id", nullable = false)
    public Category ancestor;

    @MapsId("descendantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descendant_id", nullable = false)
    public Category descendant;
}
