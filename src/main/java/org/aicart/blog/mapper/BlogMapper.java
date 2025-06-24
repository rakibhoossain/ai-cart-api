package org.aicart.blog.mapper;

import org.aicart.blog.dto.BlogDTO;
import org.aicart.blog.dto.BlogTranslationDTO;
import org.aicart.blog.entity.Blog;
import org.aicart.blog.entity.BlogCategory;
import org.aicart.blog.entity.BlogTag;
import org.aicart.blog.entity.BlogTranslation;
import org.aicart.entity.Language;
import org.aicart.media.entity.FileStorage;
import org.aicart.store.user.entity.Shop;

import java.util.stream.Collectors;

public class BlogMapper {
    
    public static BlogDTO toDto(Blog blog) {
        if (blog == null) {
            return null;
        }
        
        BlogDTO dto = new BlogDTO();
        dto.setId(blog.id);
        dto.setShopId(blog.shop.id);
        dto.setSlug(blog.slug);
        dto.setBannerId(blog.banner != null ? blog.banner.id : null);
        dto.setThumbnailId(blog.thumbnail != null ? blog.thumbnail.id : null);
        dto.setPublishedAt(blog.publishedAt);
        dto.setStatus(blog.status);
        dto.setViewCount(blog.viewCount);
        dto.setCreatedAt(blog.createdAt);
        dto.setUpdatedAt(blog.updatedAt);
        
        // Map translations
        if (blog.translations != null) {
            dto.setTranslations(blog.translations.stream()
                .map(BlogTranslationMapper::toDto)
                .collect(Collectors.toList()));
        }
        
        // Map tags
        if (blog.tags != null) {
            dto.setTags(blog.tags.stream()
                .map(BlogTagMapper::toDto)
                .collect(Collectors.toSet()));
        }
        
        // Map categories
        if (blog.categories != null) {
            dto.setCategories(blog.categories.stream()
                .map(BlogCategoryMapper::toDto)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    public static void updateEntity(Blog blog, BlogDTO dto, Shop shop) {
        // Set slug if provided or generate from default translation title
        if (dto.getSlug() != null && !dto.getSlug().isEmpty()) {
            blog.slug = dto.getSlug();
        } else if (blog.slug == null || blog.slug.isEmpty()) {
            // Get title from first translation
            if (!dto.getTranslations().isEmpty()) {
                blog.slug = generateSlug(dto.getTranslations().get(0).getTitle());
            }
        }
        
        // Set shop if not already set
        if (blog.shop == null) {
            blog.shop = shop;
        }
        
        // Set banner if provided
        if (dto.getBannerId() != null) {
            blog.banner = FileStorage.findById(dto.getBannerId());
        } else {
            blog.banner = null;
        }
        
        // Set thumbnail if provided
        if (dto.getThumbnailId() != null) {
            blog.thumbnail = FileStorage.findById(dto.getThumbnailId());
        } else {
            blog.thumbnail = null;
        }
        
        blog.status = dto.getStatus();
        
        // Set published date if status is PUBLISHED and no date is set
        if (dto.getStatus() == org.aicart.blog.entity.BlogStatus.PUBLISHED && blog.publishedAt == null) {
            blog.publishedAt = java.time.LocalDateTime.now();
        }
    }
    
    private static String generateSlug(String title) {
        if (title == null || title.isEmpty()) {
            return "";
        }
        
        // Convert to lowercase
        String slug = title.toLowerCase();
        
        // Replace spaces with hyphens
        slug = slug.replaceAll("\\s+", "-");
        
        // Remove special characters
        slug = slug.replaceAll("[^a-z0-9-]", "");
        
        // Remove multiple hyphens
        slug = slug.replaceAll("-+", "-");
        
        // Trim hyphens from start and end
        slug = slug.replaceAll("^-|-$", "");
        
        return slug;
    }
}