package org.aicart.blog.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.blog.dto.BlogTagDTO;
import org.aicart.blog.dto.BlogTagTranslationDTO;
import org.aicart.blog.entity.BlogTag;
import org.aicart.blog.entity.BlogTagTranslation;
import org.aicart.blog.mapper.BlogTagMapper;
import org.aicart.entity.Language;
import org.aicart.store.user.entity.Shop;
import org.aicart.util.SlugGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlogTagService {
    
    @Inject
    EntityManager em;
    
    @Inject
    SlugGenerator slugGenerator;
    
    public List<BlogTagDTO> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery) {
        StringBuilder jpql = new StringBuilder("SELECT t FROM BlogTag t WHERE t.shop.id = :shopId");
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            jpql.append(" AND (LOWER(t.name) LIKE LOWER(:search))");
        }
        
        jpql.append(" ORDER BY t.").append(sortField);
        if (!ascending) {
            jpql.append(" DESC");
        }
        
        TypedQuery<BlogTag> query = em.createQuery(jpql.toString(), BlogTag.class)
                .setParameter("shopId", shop.id)
                .setFirstResult(page * size)
                .setMaxResults(size);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("search", "%" + searchQuery.trim() + "%");
        }
        
        List<BlogTag> tags = query.getResultList();
        return tags.stream().map(BlogTagMapper::toDto).collect(Collectors.toList());
    }
    
    public long countByShop(Shop shop, String searchQuery) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(t) FROM BlogTag t WHERE t.shop.id = :shopId");
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            jpql.append(" AND (LOWER(t.name) LIKE LOWER(:search))");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class)
                .setParameter("shopId", shop.id);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("search", "%" + searchQuery.trim() + "%");
        }
        
        return query.getSingleResult();
    }
    
    public List<BlogTagDTO> findAllByShop(Shop shop) {
        List<BlogTag> tags = BlogTag.find("shop.id", shop.id).list();
        return tags.stream().map(BlogTagMapper::toDto).collect(Collectors.toList());
    }
    
    public BlogTagDTO findById(Long id) {
        BlogTag tag = BlogTag.findById(id);
        if (tag == null) {
            throw new NotFoundException("Tag not found with id: " + id);
        }
        return BlogTagMapper.toDto(tag);
    }
    
    @Transactional
    public BlogTagDTO create(BlogTagDTO dto, Shop shop) {
        // Validate name uniqueness within shop
        if (BlogTag.find("name = ?1 AND shop.id = ?2", dto.getName(), shop.id).count() > 0) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }
        
        BlogTag tag = new BlogTag();
        tag.name = dto.getName();
        tag.slug = slugGenerator.generateSlug(dto.getName());
        tag.shop = shop;
        
        // Check slug uniqueness
        if (BlogTag.find("slug = ?1 AND shop.id = ?2", tag.slug, shop.id).count() > 0) {
            tag.slug = tag.slug + "-" + System.currentTimeMillis();
        }
        
        // Process translations
        processTranslations(tag, dto.getTranslations());
        
        tag.persist();
        return BlogTagMapper.toDto(tag);
    }
    
    @Transactional
    public BlogTagDTO update(Long id, BlogTagDTO dto, Shop shop) {
        BlogTag tag = BlogTag.findById(id);
        if (tag == null) {
            throw new NotFoundException("Tag not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!tag.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this tag");
        }
        
        // Check name uniqueness if changed
        if (!tag.name.equals(dto.getName()) && 
            BlogTag.find("name = ?1 AND shop.id = ?2 AND id != ?3", 
                dto.getName(), shop.id, id).count() > 0) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }
        
        tag.name = dto.getName();
        
        // Update slug if name changed
        if (!tag.name.equals(dto.getName())) {
            String newSlug = slugGenerator.generateSlug(dto.getName());
            
            // Check slug uniqueness
            if (BlogTag.find("slug = ?1 AND shop.id = ?2 AND id != ?3", 
                    newSlug, shop.id, id).count() > 0) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            
            tag.slug = newSlug;
        }
        
        // Process translations
        processTranslations(tag, dto.getTranslations());
        
        tag.persist();
        return BlogTagMapper.toDto(tag);
    }
    
    @Transactional
    public void delete(Long id, Shop shop) {
        BlogTag tag = BlogTag.findById(id);
        if (tag == null) {
            throw new NotFoundException("Tag not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!tag.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this tag");
        }
        
        // Check if tag is used by any blogs
        if (!tag.blogs.isEmpty()) {
            throw new IllegalStateException("Cannot delete tag that is used by blogs");
        }
        
        tag.delete();
    }
    
    private void processTranslations(BlogTag tag, List<BlogTagTranslationDTO> translationDTOs) {
        // Create a copy of existing translations to track which ones to remove
        List<BlogTagTranslation> existingTranslations = new ArrayList<>(tag.translations);
        
        for (BlogTagTranslationDTO translationDTO : translationDTOs) {
            Language language = Language.findById(translationDTO.getLanguageId());
            if (language == null) {
                throw new NotFoundException("Language not found with id: " + translationDTO.getLanguageId());
            }
            
            // Check if translation already exists
            BlogTagTranslation existingTranslation = tag.translations.stream()
                    .filter(t -> t.language.id.equals(language.id))
                    .findFirst()
                    .orElse(null);
            
            if (existingTranslation != null) {
                // Update existing translation
                existingTranslation.name = translationDTO.getName();
                existingTranslation.description = translationDTO.getDescription();
                existingTranslations.remove(existingTranslation);
            } else {
                // Create new translation
                BlogTagTranslation newTranslation = new BlogTagTranslation();
                newTranslation.language = language;
                newTranslation.name = translationDTO.getName();
                newTranslation.description = translationDTO.getDescription();
                tag.addTranslation(newTranslation);
            }
        }
        
        // Remove translations that were not included in the update
        for (BlogTagTranslation translationToRemove : existingTranslations) {
            tag.translations.remove(translationToRemove);
            if (translationToRemove.id != null) {
                em.remove(translationToRemove);
            }
        }
    }
}