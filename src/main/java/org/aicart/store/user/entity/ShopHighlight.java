package org.aicart.store.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "shop_highlights")
public class ShopHighlight extends PanacheEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Column(name = "icon", length = 40)
    public String icon;

    @Column(name = "title", length = 255)
    public String title;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    public boolean isActive = true;

    @Column(name = "score", nullable = false, columnDefinition = "INT DEFAULT 0")
    public int score = 0;
}
