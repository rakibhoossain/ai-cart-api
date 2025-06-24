package org.aicart.page.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pages", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"slug", "shop_id"})
})
public class Page extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;
    
    @Column(nullable = false, length = 100)
    public String name;
    
    @Column(nullable = false, length = 100)
    public String slug;
    
    @Column(nullable = false)
    public boolean active = true;
    
    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<PageTranslation> translations = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addTranslation(PageTranslation translation) {
        translations.add(translation);
        translation.page = this;
    }
}