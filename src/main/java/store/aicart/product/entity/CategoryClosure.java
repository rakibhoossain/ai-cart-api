package store.aicart.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "category_closure")
@IdClass(CategoryClosureId.class)
public class CategoryClosure extends PanacheEntityBase {

    @Id
    @Column(name = "ancestor_id", nullable = false, updatable = false)
    public Long ancestorId;

    @Id
    @Column(name = "descendant_id", nullable = false, updatable = false)
    public Long descendantId;

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
