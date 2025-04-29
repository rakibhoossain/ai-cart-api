package org.aicart.store.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.aicart.media.entity.FileStorage;
import org.aicart.store.user.dto.BannerBackgroundEnum;
import org.aicart.store.user.dto.BannerButtonDto;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "banners")
public class Banner extends PanacheEntity {

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Column(nullable = false, length = 150)
    public String tag;

    @Column(nullable = false, length = 255)
    public String title;

    @Column(length = 255)
    public String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "background_type", nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
    public BannerBackgroundEnum backgroundType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id")
    public FileStorage background;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    public FileStorage poster;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    public BannerButtonDto button;

    @Column(length = 255)
    public String url;

    @Column(name = "sort_order")
    public Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    public Boolean isActive = true;

    @Column(name = "start_date")
    public LocalDateTime startDate;

    @Column(name = "end_date")
    public LocalDateTime endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


