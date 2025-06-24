package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blog_categories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "shop_id"}),
    @UniqueConstraint(columnNames = {"slug", "shop_id"})
})
public class BlogCategory extends PanacheEntity {

    @Column(nullable = false, length = 100)
    public String name;

    @Column(nullable = false, length = 100)
    public String slug;

    @Column(length = 500)
    public String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public BlogCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public Set<BlogCategory> children = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @ManyToMany(mappedBy = "categories")
    public Set<Blog> blogs = new HashSet<>();

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