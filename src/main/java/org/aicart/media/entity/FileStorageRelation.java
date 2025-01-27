package org.aicart.media.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "file_storage_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"associated_type", "associated_id", "file_id"})
    )
public class FileStorageRelation extends PanacheEntity {

    @Column(name = "associated_type", nullable = false)
    private int associatedType; // e.g., 1 => "Product", 2 => "Category"

    @Column(name = "associated_id", nullable = false)
    private Long associatedId; // ID of the associated entity

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "file_id", nullable = false)
    private FileStorage file;

    @Column(name = "score", nullable = false, columnDefinition = "INT DEFAULT 0")
    public int score = 0;
}
