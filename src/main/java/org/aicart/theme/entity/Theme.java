package org.aicart.theme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.media.entity.FileStorage;

@Entity
@Table(name = "themes")
public class Theme extends PanacheEntity {

    @Column(name = "name", nullable = false, length = 50)
    public String name;

    @Column(name = "description")
    public String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    public FileStorage thumbnail;

    @Column(name = "price")
    public int price = 0;
}
