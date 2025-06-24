package org.aicart.blog.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.blog.dto.BlogDTO;
import org.aicart.blog.dto.BlogTranslationDTO;
import org.aicart.blog.entity.*;
import org.aicart.blog.mapper.BlogMapper;
import org.aicart.blog.mapper.BlogTranslationMapper;
import org.aicart.blog.repository.BlogCategoryRepository;
import org.aicart.blog.repository.BlogRepository;
import org.aicart.blog.repository.BlogTagRepository;
import org.aicart.entity.Language;
import org.aicart.media.entity.FileStorage;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlogService {
    
    @Inject
    BlogRepository blogRepository;
    
    @Inject
    BlogTagRepository tagRepository;
    
    @Inject
    BlogCategoryRepository categoryRepository;
    
    public List<BlogDTO> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery, Language language) {
        return blogRepository.findByShop(shop, page, size, sortField, ascending, searchQuery, language)
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public long countByShop(Shop shop, String searchQuery, Language language) {
        return blogRepository.countByShop(shop, searchQuery, language);
    }
    
    public BlogDTO findById(Long id, Language language) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new NotFoundException("Blog not found with id: " + id);
        }
        return BlogMapper.toDto(blog);
    }
    
    public BlogDTO findBySlug(String slug, Shop shop, Language language) {
        Blog blog = blogRepository.findBySlug(slug, shop)
                .orElseThrow(() -> new NotFoundException("Blog not found with slug: " + slug));
        return BlogMapper.toDto(blog);
    }
    
    @Transactional
    public BlogDTO create(BlogDTO dto, Shop shop) {
        Blog blog = new Blog();
        blog.shop = shop;
        
        // Set basic properties
        BlogMapper.updateEntity(blog, dto, shop);
        
        // Process translations
        processTranslations(blog, dto.getTranslations());
        
        // Process tags
        processTagsAndCategories(blog, dto);
        
        // Save the blog
        blogRepository.persist(blog);
        
        return BlogMapper.toDto(blog);
    }
    
    @Transactional
    public BlogDTO update(Long id, BlogDTO dto, Shop shop) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new NotFoundException("Blog not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this blog");
        }
        
        // Update basic properties
        BlogMapper.updateEntity(blog, dto, shop);
        
        // Process translations
        processTranslations(blog, dto.getTranslations());
        
        // Process tags and categories
        processTagsAndCategories(blog, dto);
        
        // Save the blog
        blogRepository.persist(blog);
        
        return BlogMapper.toDto(blog);
    }
    
    @Transactional
    public void delete(Long id, Shop shop) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new NotFoundException("Blog not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this blog");
        }
        
        blogRepository.delete(blog);
    }
    
    @Transactional
    public BlogDTO updateStatus(Long id, BlogStatus status, Shop shop) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new NotFoundException("Blog not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this blog");
        }
        
        blog.status = status;
        
        // If publishing for the first time, set published date
        if (status == BlogStatus.PUBLISHED && blog.publishedAt == null) {
            blog.publishedAt = LocalDateTime.now();
        }
        
        return BlogMapper.toDto(blog);
    }
    
    @Transactional
    public void incrementViewCount(Long id) {
        Blog blog = blogRepository.findById(id);
        if (blog != null) {
            blog.viewCount++;
            blogRepository.persist(blog);
        }
    }
    
    private void processTranslations(Blog blog, List<BlogTranslationDTO> translationDTOs) {
        // Create a copy of existing translations to track which ones to remove
        List<BlogTranslation> existingTranslations = new ArrayList<>(blog.translations);
        
        for (BlogTranslationDTO translationDTO : translationDTOs) {
            Language language = Language.findById(translationDTO.getLanguageId());
            if (language == null) {
                throw new NotFoundException("Language not found with id: " + translationDTO.getLanguageId());
            }
            
            // Check if translation already exists
            BlogTranslation existingTranslation = blog.translations.stream()
                    .filter(t -> t.language.id.equals(language.id))
                    .findFirst()
                    .orElse(null);
            
            if (existingTranslation != null) {
                // Update existing translation
                BlogTranslationMapper.updateEntity(existingTranslation, translationDTO, language);
                existingTranslations.remove(existingTranslation);
            } else {
                // Create new translation
                BlogTranslation newTranslation = BlogTranslationMapper.createEntity(translationDTO, language);
                blog.addTranslation(newTranslation);
            }
        }
        
        // Remove translations that weren't in the update
        for (BlogTranslation translationToRemove : existingTranslations) {
            blog.removeTranslation(translationToRemove);
        }
    }
    
    private void processTagsAndCategories(Blog blog, BlogDTO dto) {
        // Process tags
        Set<BlogTag> newTags = new HashSet<>();
        if (dto.getTags() != null) {
            for (var tagDto : dto.getTags()) {
                BlogTag tag;
                if (tagDto.getId() != null) {
                    tag = tagRepository.findById(tagDto.getId());
                    if (tag == null) {
                        throw new NotFoundException("Tag not found with id: " + tagDto.getId());
                    }
                } else if (tagDto.getName() != null) {
                    // Try to find by name or create new
                    tag = tagRepository.findByName(tagDto.getName(), blog.shop)
                            .orElseGet(() -> {
                                BlogTag newTag = new BlogTag();
                                newTag.name = tagDto.getName();
                                newTag.shop = blog.shop;
                                newTag.slug = generateSlug(tagDto.getName());
                                tagRepository.persist(newTag);
                                return newTag;
                            });
                } else {
                    continue; // Skip invalid tag
                }
                newTags.add(tag);
            }
        }
        
        // Remove old tags
        Set<BlogTag> tagsToRemove = new HashSet<>(blog.tags);
        tagsToRemove.removeAll(newTags);
        for (BlogTag tag : tagsToRemove) {
            blog.removeTag(tag);
        }
        
        // Add new tags
        for (BlogTag tag : newTags) {
            if (!blog.tags.contains(tag)) {
                blog.addTag(tag);
            }
        }
        
        // Process categories
        Set<BlogCategory> newCategories = new HashSet<>();
        if (dto.getCategories() != null) {
            for (var categoryDto : dto.getCategories()) {
                if (categoryDto.getId() != null) {
                    BlogCategory category = categoryRepository.findById(categoryDto.getId());
                    if (category == null) {
                        throw new NotFoundException("Category not found with id: " + categoryDto.getId());
                    }
                    newCategories.add(category);
                }
            }
        }
        
        // Remove old categories
        Set<BlogCategory> categoriesToRemove = new HashSet<>(blog.categories);
        categoriesToRemove.removeAll(newCategories);
        for (BlogCategory category : categoriesToRemove) {
            blog.removeCategory(category);
        }
        
        // Add new categories
        for (BlogCategory category : newCategories) {
            if (!blog.categories.contains(category)) {
                blog.addCategory(category);
            }
        }
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
    
    public List<BlogDTO> findPublishedByShop(Shop shop, int page, int size, Language language) {
        return blogRepository.findPublishedByShop(shop, page, size, language)
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<BlogDTO> findByCategory(Long categoryId, Shop shop, int page, int size, Language language) {
        return blogRepository.findByCategory(categoryId, shop, page, size, language)
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<BlogDTO> findByTag(Long tagId, Shop shop, int page, int size, Language language) {
        return blogRepository.findByTag(tagId, shop, page, size, language)
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }
}