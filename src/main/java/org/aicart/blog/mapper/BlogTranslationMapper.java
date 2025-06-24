package org.aicart.blog.mapper;

import org.aicart.blog.dto.BlogTranslationDTO;
import org.aicart.blog.entity.BlogTranslation;
import org.aicart.entity.Language;

public class BlogTranslationMapper {
    
    public static BlogTranslationDTO toDto(BlogTranslation translation) {
        if (translation == null) {
            return null;
        }
        
        BlogTranslationDTO dto = new BlogTranslationDTO();
        dto.setId(translation.id);
        dto.setLanguageId(translation.language.id);
        dto.setLanguageCode(translation.language.code);
        dto.setTitle(translation.title);
        dto.setContent(translation.content);
        dto.setShortDescription(translation.shortDescription);
        dto.setMetaTitle(translation.metaTitle);
        dto.setMetaDescription(translation.metaDescription);
        dto.setMetaKeywords(translation.metaKeywords);
        dto.setCreatedAt(translation.createdAt);
        dto.setUpdatedAt(translation.updatedAt);
        
        return dto;
    }
    
    public static void updateEntity(BlogTranslation translation, BlogTranslationDTO dto, Language language) {
        translation.language = language;
        translation.title = dto.getTitle();
        translation.content = dto.getContent();
        translation.shortDescription = dto.getShortDescription();
        translation.metaTitle = dto.getMetaTitle();
        translation.metaDescription = dto.getMetaDescription();
        translation.metaKeywords = dto.getMetaKeywords();
    }
    
    public static BlogTranslation createEntity(BlogTranslationDTO dto, Language language) {
        BlogTranslation translation = new BlogTranslation();
        updateEntity(translation, dto, language);
        return translation;
    }
}