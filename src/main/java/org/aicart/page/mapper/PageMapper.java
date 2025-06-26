package org.aicart.page.mapper;

import org.aicart.entity.Language;
import org.aicart.page.dto.PageDTO;
import org.aicart.page.dto.PageTranslationDTO;
import org.aicart.page.entity.Page;
import org.aicart.page.entity.PageTranslation;
import org.aicart.store.user.entity.Shop;

import java.util.stream.Collectors;

public class PageMapper {
    
    public static PageDTO toDto(Page entity) {
        if (entity == null) {
            return null;
        }
        
        PageDTO dto = new PageDTO();
        dto.setId(entity.id);
        dto.setName(entity.name);
        dto.setSlug(entity.slug);
        dto.setActive(entity.active);
        dto.setCreatedAt(entity.createdAt);
        dto.setUpdatedAt(entity.updatedAt);
        
        // Map translations
        dto.setTranslations(entity.translations.stream()
                .map(PageMapper::translationToDto)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    public static PageTranslationDTO translationToDto(PageTranslation entity) {
        if (entity == null) {
            return null;
        }
        
        PageTranslationDTO dto = new PageTranslationDTO();
        dto.setId(entity.id);
        dto.setLanguageId(entity.language.id);
        dto.setLanguageName(entity.language.name);
        dto.setLanguageCode(entity.language.code);
        dto.setTitle(entity.title);
        dto.setContent(entity.content);
        dto.setMetaTitle(entity.metaTitle);
        dto.setMetaDescription(entity.metaDescription);
        dto.setMetaKeywords(entity.metaKeywords);
        dto.setCreatedAt(entity.createdAt);
        dto.setUpdatedAt(entity.updatedAt);
        
        return dto;
    }
    
    public static void updateEntity(Page entity, PageDTO dto, Shop shop) {
        entity.name = dto.getName();
        entity.active = dto.isActive();
        entity.shop = shop;
        
        // Slug will be handled by the service
    }
}