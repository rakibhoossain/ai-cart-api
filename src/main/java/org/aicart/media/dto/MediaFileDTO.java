package org.aicart.media.dto;

import java.time.LocalDateTime;

public class MediaFileDTO {
    private Long id;
    private String fileName;
    private String originalUrl;
    private String thumbnailUrl;
    private String mediumUrl;
    private String fileType;
    private String mimeType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String altText;
    private String storageLocation;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public MediaFileDTO() {}

    public MediaFileDTO(Long id, String fileName, String originalUrl, String thumbnailUrl, 
                       String mediumUrl, String fileType, String mimeType, Long fileSize,
                       Integer width, Integer height, String altText, String storageLocation,
                       String metadata, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.originalUrl = originalUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.mediumUrl = mediumUrl;
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.altText = altText;
        this.storageLocation = storageLocation;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getMediumUrl() { return mediumUrl; }
    public void setMediumUrl(String mediumUrl) { this.mediumUrl = mediumUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
