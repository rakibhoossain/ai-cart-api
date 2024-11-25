package store.aicart.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "category_closure")
public class CategoryClosure extends PanacheEntityBase {

    @EmbeddedId
    public CategoryClosureId id;

    @MapsId("ancestorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_id", nullable = false)
    public Category ancestor;

    @MapsId("descendantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descendant_id", nullable = false)
    public Category descendant;

    @Column(nullable = false)
    public int depth;

    public CategoryClosure() {}

    public CategoryClosure(Category ancestor, Category descendant, int depth) {
        this.id = new CategoryClosureId(ancestor.id, descendant.id);
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.depth = depth;
    }
}
