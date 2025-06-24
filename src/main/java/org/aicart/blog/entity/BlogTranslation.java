package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_translations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"blog_id", "language_id"})
})
public class BlogTranslation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    public Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;

    @Column(nullable = false, length = 255)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;

    @Column(name = "short_description", length = 500)
    public String shortDescription;

    @Column(name = "meta_title", length = 255)
    public String metaTitle;

    @Column(name = "meta_description", length = 500)
    public String metaDescription;

    @Column(name = "meta_keywords", length = 255)
    public String metaKeywords;

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