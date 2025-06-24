package org.aicart.blog.mapper;

import org.aicart.blog.dto.BlogCategoryDTO;
import org.aicart.blog.entity.BlogCategory;
import org.aicart.store.user.entity.Shop;

import java.util.stream.Collectors;

public class BlogCategoryMapper {
    
    public static BlogCategoryDTO toDto(BlogCategory category) {
        if (category == null) {
            return null;
        }
        
        BlogCategoryDTO dto = new BlogCategoryDTO();
        dto.setId(category.id);
        dto.setName(category.name);
        dto.setSlug(category.slug);
        dto.setDescription(category.description);
        dto.setParentId(category.parent != null ? category.parent.id : null);
        dto.setShopId(category.shop.id);
        dto.setCreatedAt(category.createdAt);
        dto.setUpdatedAt(category.updatedAt);
        
        // Map children (if needed)
        if (category.children != null && !category.children.isEmpty()) {
            dto.setChildren(category.children.stream()
                .map(BlogCategoryMapper::toDto)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    public static void updateEntity(BlogCategory category, BlogCategoryDTO dto, Shop shop) {
        category.name = dto.getName();
        category.description = dto.getDescription();
        
        // Only update slug if it's not already set
        if (category.slug == null || category.slug.isEmpty()) {
            category.slug = generateSlug(dto.getName());
        } else if (dto.getSlug() != null && !dto.getSlug().isEmpty()) {
            category.slug = dto.getSlug();
        }
        
        // Set shop if not already set
        if (category.shop == null) {
            category.shop = shop;
        }
    }
    
    private static String generateSlug(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        
        // Convert to lowercase
        String slug = name.toLowerCase();
        
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