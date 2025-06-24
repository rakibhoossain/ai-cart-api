package org.aicart.blog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.aicart.blog.entity.BlogStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlogDTO {
    private Long id;
    
    private Long shopId;
    
    private String slug;
    
    private Long bannerId;
    
    private Long thumbnailId;
    
    @NotEmpty(message = "At least one translation is required")
    @Valid
    private List<BlogTranslationDTO> translations = new ArrayList<>();
    
    private Set<BlogTagDTO> tags = new HashSet<>();
    
    private Set<BlogCategoryDTO> categories = new HashSet<>();
    
    private LocalDateTime publishedAt;
    
    @NotNull(message = "Status is required")
    private BlogStatus status = BlogStatus.DRAFT;
    
    private Long viewCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getBannerId() {
        return bannerId;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }

    public Long getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(Long thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public List<BlogTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(List<BlogTranslationDTO> translations) {
        this.translations = translations;
    }

    public Set<BlogTagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<BlogTagDTO> tags) {
        this.tags = tags;
    }

    public Set<BlogCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<BlogCategoryDTO> categories) {
        this.categories = categories;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public BlogStatus getStatus() {
        return status;
    }

    public void setStatus(BlogStatus status) {
        this.status = status;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}