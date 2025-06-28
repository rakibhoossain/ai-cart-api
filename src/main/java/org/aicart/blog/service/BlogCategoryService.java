package org.aicart.blog.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.blog.dto.BlogCategoryDTO;
import org.aicart.blog.entity.BlogCategory;
import org.aicart.blog.mapper.BlogCategoryMapper;
import org.aicart.blog.repository.BlogCategoryRepository;
import org.aicart.store.user.entity.Shop;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlogCategoryService {
    
    @Inject
    BlogCategoryRepository categoryRepository;
    
    public List<BlogCategoryDTO> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery) {
        return categoryRepository.findByShop(shop, page, size, sortField, ascending, searchQuery)
                .stream()
                .map(BlogCategoryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public long countByShop(Shop shop, String searchQuery) {
        return categoryRepository.countByShop(shop, searchQuery);
    }
    
    public BlogCategoryDTO findById(Long id) {
        BlogCategory category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException("Category not found with id: " + id);
        }
        return BlogCategoryMapper.toDto(category);
    }
    
    public List<BlogCategoryDTO> findRootCategories(Shop shop) {
        return categoryRepository.findRootCategories(shop)
                .stream()
                .map(BlogCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BlogCategoryDTO> findRootCategoriesWithCounts(Shop shop) {
        return categoryRepository.findRootCategories(shop)
                .stream()
                .map(category -> {
                    BlogCategoryDTO dto = BlogCategoryMapper.toDto(category);
                    // Add post count for this category
                    long postCount = categoryRepository.countPublishedBlogsByCategory(category);
                    dto.setPostCount(postCount);
                    return dto;
                })
                .filter(dto -> dto.getPostCount() > 0) // Only show categories with posts
                .collect(Collectors.toList());
    }

    public List<BlogCategoryDTO> findAllCategories(Shop shop) {
        return categoryRepository.findAllByShop(shop)
                .stream()
                .map(BlogCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BlogCategoryDTO> findAllCategoriesWithCounts(Shop shop) {
        return categoryRepository.findAllByShop(shop)
                .stream()
                .map(category -> {
                    BlogCategoryDTO dto = BlogCategoryMapper.toDto(category);
                    // Add post count for this category
                    long postCount = categoryRepository.countPublishedBlogsByCategory(category);
                    dto.setPostCount(postCount);
                    return dto;
                })
                .filter(dto -> dto.getPostCount() > 0) // Only show categories with posts
                .collect(Collectors.toList());
    }
    
    public List<BlogCategoryDTO> findChildren(Long parentId) {
        BlogCategory parent = categoryRepository.findById(parentId);
        if (parent == null) {
            throw new NotFoundException("Parent category not found with id: " + parentId);
        }
        
        return categoryRepository.findChildren(parent)
                .stream()
                .map(BlogCategoryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BlogCategoryDTO create(BlogCategoryDTO dto, Shop shop) {
        // Check if name already exists for this shop
        if (categoryRepository.findByName(dto.getName(), shop).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + dto.getName() + "' already exists");
        }
        
        BlogCategory category = new BlogCategory();
        category.shop = shop;
        category.name = dto.getName();
        
        // Generate slug if not provided
        if (dto.getSlug() == null || dto.getSlug().isEmpty()) {
            category.slug = generateSlug(dto.getName());
        } else {
            category.slug = dto.getSlug();
        }
        
        // Set parent if provided
        if (dto.getParentId() != null) {
            BlogCategory parent = categoryRepository.findById(dto.getParentId());
            if (parent == null) {
                throw new NotFoundException("Parent category not found with id: " + dto.getParentId());
            }
            category.parent = parent;
        }
        
        categoryRepository.persist(category);
        return BlogCategoryMapper.toDto(category);
    }
    
    @Transactional
    public BlogCategoryDTO update(Long id, BlogCategoryDTO dto, Shop shop) {
        BlogCategory category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException("Category not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!category.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this category");
        }
        
        // Check if name is being changed and if it already exists
        if (!category.name.equals(dto.getName()) && 
                categoryRepository.findByName(dto.getName(), shop).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + dto.getName() + "' already exists");
        }
        
        category.name = dto.getName();
        
        // Update slug if provided
        if (dto.getSlug() != null && !dto.getSlug().isEmpty()) {
            category.slug = dto.getSlug();
        }
        
        // Update parent if provided
        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(category.id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            BlogCategory parent = categoryRepository.findById(dto.getParentId());
            if (parent == null) {
                throw new NotFoundException("Parent category not found with id: " + dto.getParentId());
            }
            
            // Check for circular reference
            BlogCategory current = parent;
            while (current != null) {
                if (current.id.equals(category.id)) {
                    throw new IllegalArgumentException("Circular reference detected in category hierarchy");
                }
                current = current.parent;
            }
            
            category.parent = parent;
        } else {
            category.parent = null;
        }
        
        categoryRepository.persist(category);
        return BlogCategoryMapper.toDto(category);
    }
    
    @Transactional
    public void delete(Long id, Shop shop) {
        BlogCategory category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException("Category not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!category.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this category");
        }
        
        // Check if category has children
        List<BlogCategory> children = categoryRepository.findChildren(category);
        if (!children.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with children. Delete children first or reassign them.");
        }
        
        categoryRepository.delete(category);
    }
    
    private String generateSlug(String name) {
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