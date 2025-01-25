package org.aicart.media.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_storage")
public class FileStorage extends PanacheEntity {

    @Column(name = "file_name", nullable = false)
    public String fileName;

    @Column(name = "file_type", nullable = false, length = 20)
    public String fileType; // 'image', 'zip', 'document', etc.

    @Column(name = "mime_type", nullable = false, length = 50)
    public String mimeType;

    @Column(name = "original_url", nullable = false)
    public String originalUrl;

    @Column(name = "thumbnail_url")
    public String thumbnailUrl;

    @Column(name = "medium_url")
    public String mediumUrl;

    @Column(name = "file_size", nullable = false)
    public Long fileSize;

    @Column(name = "width")
    public Integer width;

    @Column(name = "height")
    public Integer height;

    @Column(name = "alt_text")
    public String altText;

    @Column(name = "storage_location", nullable = false)
    public String storageLocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}