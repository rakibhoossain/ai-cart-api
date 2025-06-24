package org.aicart.page.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;

import java.time.LocalDateTime;

@Entity
@Table(name = "page_translations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"page_id", "language_id"})
})
public class PageTranslation extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    public Page page;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    public Language language;
    
    @Column(nullable = false, length = 200)
    public String title;
    
    @Column(columnDefinition = "TEXT")
    public String content;
    
    @Column(name = "meta_title", length = 200)
    public String metaTitle;
    
    @Column(name = "meta_description", length = 500)
    public String metaDescription;
    
    @Column(name = "meta_keywords", length = 200)
    public String metaKeywords;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}