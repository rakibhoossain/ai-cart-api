package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_tag_translations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"blog_tag_id", "language_id"})
})
public class BlogTagTranslation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_tag_id", nullable = false)
    public BlogTag blogTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;

    @Column(nullable = false, length = 100)
    public String name;

    @Column(length = 500)
    public String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}