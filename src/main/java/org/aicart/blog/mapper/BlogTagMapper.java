package org.aicart.blog.mapper;

import org.aicart.blog.dto.BlogTagDTO;
import org.aicart.blog.dto.BlogTagTranslationDTO;
import org.aicart.blog.entity.BlogTag;
import org.aicart.blog.entity.BlogTagTranslation;

import java.util.stream.Collectors;

public class BlogTagMapper {
    
    public static BlogTagDTO toDto(BlogTag entity) {
        if (entity == null) {
            return null;
        }
        
        BlogTagDTO dto = new BlogTagDTO();
        dto.setId(entity.id);
        dto.setName(entity.name);
        dto.setSlug(entity.slug);
        dto.setShopId(entity.shop.id);
        dto.setCreatedAt(entity.createdAt);
        dto.setUpdatedAt(entity.updatedAt);
        
        // Map translations
        dto.setTranslations(entity.translations.stream()
                .map(BlogTagMapper::translationToDto)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    public static BlogTagTranslationDTO translationToDto(BlogTagTranslation entity) {
        if (entity == null) {
            return null;
        }
        
        BlogTagTranslationDTO dto = new BlogTagTranslationDTO();
        dto.setId(entity.id);
        dto.setLanguageId(entity.language.id);
        dto.setLanguageName(entity.language.name);
        dto.setName(entity.name);
        dto.setDescription(entity.description);
        dto.setCreatedAt(entity.createdAt);
        dto.setUpdatedAt(entity.updatedAt);
        
        return dto;
    }
}
