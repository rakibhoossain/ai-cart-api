package org.aicart.media.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.media.FileAssociation;
import org.aicart.store.product.entity.Category;
import org.aicart.store.product.entity.Product;

@Entity
@Table(name = "file_storage_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"associated_type", "associated_id", "file_id"})
    )
public class FileStorageRelation extends PanacheEntity {

    @Column(name = "associated_type", nullable = false)
    public int associatedType; // e.g., 1 => "Product", 2 => "Category"

    @Column(name = "associated_id", nullable = false)
    public Long associatedId; // ID of the associated entity

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "associated_id", referencedColumnName = "id", insertable = false, updatable = false)
    public Product product; // Explicit relationship

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "associated_id", referencedColumnName = "id", insertable = false, updatable = false)
    public Category category; // Explicit relationship

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    public FileStorage file;

    @Column(name = "score", nullable = false, columnDefinition = "INT DEFAULT 0")
    public int score = 0;

    @PrePersist
    public void prePersist() {
        if (product != null) {
            associatedId = product.id;
            associatedType = FileAssociation.PRODUCT.getValue();
        } else if (category != null) {
            associatedId = category.id;
            associatedType = FileAssociation.CATEGORY.getValue();
        }
    }
}
