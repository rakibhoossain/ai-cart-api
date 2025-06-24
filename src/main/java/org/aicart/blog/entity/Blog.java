package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Language;
import org.aicart.media.entity.FileStorage;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "blogs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"slug", "shop_id"})
})
public class Blog extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Column(nullable = false, length = 255)
    public String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id")
    public FileStorage banner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    public FileStorage thumbnail;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BlogTranslation> translations = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "blog_tags",
        joinColumns = @JoinColumn(name = "blog_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    public Set<BlogTag> tags = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "blog_categories",
        joinColumns = @JoinColumn(name = "blog_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    public Set<BlogCategory> categories = new HashSet<>();

    @Column(name = "published_at")
    public LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public BlogStatus status = BlogStatus.DRAFT;

    @Column(name = "view_count")
    public Long viewCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addTranslation(BlogTranslation translation) {
        translations.add(translation);
        translation.blog = this;
    }

    public void removeTranslation(BlogTranslation translation) {
        translations.remove(translation);
        translation.blog = null;
    }

    public BlogTranslation getTranslation(Language language) {
        return translations.stream()
            .filter(t -> t.language.id.equals(language.id))
            .findFirst()
            .orElse(null);
    }

    public BlogTranslation getDefaultTranslation() {
        return translations.isEmpty() ? null : translations.get(0);
    }

    public void addTag(BlogTag tag) {
        tags.add(tag);
        tag.blogs.add(this);
    }

    public void removeTag(BlogTag tag) {
        tags.remove(tag);
        tag.blogs.remove(this);
    }

    public void addCategory(BlogCategory category) {
        categories.add(category);
        category.blogs.add(this);
    }

    public void removeCategory(BlogCategory category) {
        categories.remove(category);
        category.blogs.remove(this);
    }
}