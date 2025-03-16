package org.aicart.store.product.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.media.entity.FileStorageRelation;
import org.aicart.store.product.ProductStatusEnum;
import org.aicart.store.user.entity.Shop;

import java.util.List;

@Entity
@Table(name = "product_collections")
public class ProductCollection extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public ProductStatusEnum status = ProductStatusEnum.DRAFT;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(length = 255, unique = true, nullable = false)
    public String slug;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(name = "meta_title", length = 255)
    public String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    public String metaDescription;

    @OneToMany(mappedBy = "associatedId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<FileStorageRelation> fileRelations;
}
