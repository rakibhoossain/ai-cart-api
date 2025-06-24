package org.aicart.page.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.entity.Language;
import org.aicart.page.dto.PageDTO;
import org.aicart.page.dto.PageTranslationDTO;
import org.aicart.page.entity.Page;
import org.aicart.page.entity.PageTranslation;
import org.aicart.page.mapper.PageMapper;
import org.aicart.store.user.entity.Shop;
import org.aicart.util.SlugGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PageService {
    
    @Inject
    EntityManager em;
    
    @Inject
    SlugGenerator slugGenerator;
    
    public List<PageDTO> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Page p WHERE p.shop.id = :shopId");
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE LOWER(:search))");
        }
        
        jpql.append(" ORDER BY p.").append(sortField);
        if (!ascending) {
            jpql.append(" DESC");
        }
        
        TypedQuery<Page> query = em.createQuery(jpql.toString(), Page.class)
                .setParameter("shopId", shop.id)
                .setFirstResult(page * size)
                .setMaxResults(size);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("search", "%" + searchQuery.trim() + "%");
        }
        
        List<Page> pages = query.getResultList();
        return pages.stream().map(PageMapper::toDto).collect(Collectors.toList());
    }
    
    public long countByShop(Shop shop, String searchQuery) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Page p WHERE p.shop.id = :shopId");
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE LOWER(:search))");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class)
                .setParameter("shopId", shop.id);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("search", "%" + searchQuery.trim() + "%");
        }
        
        return query.getSingleResult();
    }
    
    public PageDTO findById(Long id, Language language) {
        Page page = Page.findById(id);
        if (page == null) {
            throw new NotFoundException("Page not found with id: " + id);
        }
        return PageMapper.toDto(page);
    }
    
    public PageDTO findBySlug(String slug, Shop shop, Language language) {
        Page page = Page.find("slug = ?1 AND shop.id = ?2 AND active = true", slug, shop.id).firstResult();
        if (page == null) {
            throw new NotFoundException("Page not found with slug: " + slug);
        }
        return PageMapper.toDto(page);
    }
    
    @Transactional
    public PageDTO create(PageDTO dto, Shop shop) {
        // Validate name uniqueness within shop
        if (Page.find("name = ?1 AND shop.id = ?2", dto.getName(), shop.id).count() > 0) {
            throw new IllegalArgumentException("Page with this name already exists");
        }
        
        Page page = new Page();
        PageMapper.updateEntity(page, dto, shop);
        
        // Generate slug if not provided
        if (dto.getSlug() == null || dto.getSlug().isEmpty()) {
            page.slug = slugGenerator.generateSlug(dto.getName());
        } else {
            page.slug = slugGenerator.generateSlug(dto.getSlug());
        }
        
        // Check slug uniqueness
        if (Page.find("slug = ?1 AND shop.id = ?2", page.slug, shop.id).count() > 0) {
            page.slug = page.slug + "-" + System.currentTimeMillis();
        }
        
        // Process translations
        processTranslations(page, dto.getTranslations());
        
        page.persist();
        return PageMapper.toDto(page);
    }
    
    @Transactional
    public PageDTO update(Long id, PageDTO dto, Shop shop) {
        Page page = Page.findById(id);
        if (page == null) {
            throw new NotFoundException("Page not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!page.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this page");
        }
        
        // Check name uniqueness if changed
        if (!page.name.equals(dto.getName()) && 
            Page.find("name = ?1 AND shop.id = ?2 AND id != ?3", 
                dto.getName(), shop.id, id).count() > 0) {
            throw new IllegalArgumentException("Page with this name already exists");
        }
        
        PageMapper.updateEntity(page, dto, shop);
        
        // Update slug if name changed or slug provided
        if ((!page.name.equals(dto.getName()) && (dto.getSlug() == null || dto.getSlug().isEmpty())) || 
            (dto.getSlug() != null && !dto.getSlug().isEmpty() && !page.slug.equals(dto.getSlug()))) {
            
            String newSlug = dto.getSlug() != null && !dto.getSlug().isEmpty() ? 
                    slugGenerator.generateSlug(dto.getSlug()) : 
                    slugGenerator.generateSlug(dto.getName());
            
            // Check slug uniqueness
            if (Page.find("slug = ?1 AND shop.id = ?2 AND id != ?3", 
                    newSlug, shop.id, id).count() > 0) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            
            page.slug = newSlug;
        }
        
        // Process translations
        processTranslations(page, dto.getTranslations());
        
        page.persist();
        return PageMapper.toDto(page);
    }
    
    @Transactional
    public void delete(Long id, Shop shop) {
        Page page = Page.findById(id);
        if (page == null) {
            throw new NotFoundException("Page not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!page.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this page");
        }
        
        page.delete();
    }
    
    @Transactional
    public PageDTO updateStatus(Long id, boolean active, Shop shop) {
        Page page = Page.findById(id);
        if (page == null) {
            throw new NotFoundException("Page not found with id: " + id);
        }
        
        // Verify shop ownership
        if (!page.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this page");
        }
        
        page.active = active;
        page.persist();
        
        return PageMapper.toDto(page);
    }
    
    private void processTranslations(Page page, List<PageTranslationDTO> translationDTOs) {
        // Create a copy of existing translations to track which ones to remove
        List<PageTranslation> existingTranslations = new ArrayList<>(page.translations);
        
        for (PageTranslationDTO translationDTO : translationDTOs) {
            Language language = Language.findById(translationDTO.getLanguageId());
            if (language == null) {
                throw new NotFoundException("Language not found with id: " + translationDTO.getLanguageId());
            }
            
            // Check if translation already exists
            PageTranslation existingTranslation = page.translations.stream()
                    .filter(t -> t.language.id.equals(language.id))
                    .findFirst()
                    .orElse(null);
            
            if (existingTranslation != null) {
                // Update existing translation
                existingTranslation.title = translationDTO.getTitle();
                existingTranslation.content = translationDTO.getContent();
                existingTranslation.metaTitle = translationDTO.getMetaTitle();
                existingTranslation.metaDescription = translationDTO.getMetaDescription();
                existingTranslation.metaKeywords = translationDTO.getMetaKeywords();
                existingTranslations.remove(existingTranslation);
            } else {
                // Create new translation
                PageTranslation newTranslation = new PageTranslation();
                newTranslation.language = language;
                newTranslation.title = translationDTO.getTitle();
                newTranslation.content = translationDTO.getContent();
                newTranslation.metaTitle = translationDTO.getMetaTitle();
                newTranslation.metaDescription = translationDTO.getMetaDescription();
                newTranslation.metaKeywords = translationDTO.getMetaKeywords();
                page.addTranslation(newTranslation);
            }
        }
        
        // Remove translations that were not included in the update
        for (PageTranslation translationToRemove : existingTranslations) {
            page.translations.remove(translationToRemove);
            if (translationToRemove.id != null) {
                em.remove(translationToRemove);
            }
        }
    }
}