package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "blog_tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "shop_id"}),
    @UniqueConstraint(columnNames = {"slug", "shop_id"})
})
public class BlogTag extends PanacheEntity {

    @Column(nullable = false, length = 50)
    public String name;

    @Column(nullable = false, length = 50)
    public String slug;

    @Column(length = 7)
    public String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @ManyToMany(mappedBy = "tags")
    public Set<Blog> blogs = new HashSet<>();

    @OneToMany(mappedBy = "blogTag", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BlogTagTranslation> translations = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTranslation(BlogTagTranslation translation) {
        translations.add(translation);
        translation.blogTag = this;
    }
}
