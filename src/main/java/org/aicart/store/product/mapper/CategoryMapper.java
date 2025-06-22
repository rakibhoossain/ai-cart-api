package org.aicart.store.product.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.product.dto.CategoryDTO;
import org.aicart.store.product.entity.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CategoryMapper {

    public CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.id);
        dto.setName(category.name);
        
        if (category.parentCategory != null) {
            dto.setParentId(category.parentCategory.id);
            dto.setParentName(category.parentCategory.name);
        }
        
        return dto;
    }
    
    public List<CategoryDTO> toDtoList(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        
        List<CategoryDTO> dtos = new ArrayList<>(categories.size());
        for (Category category : categories) {
            dtos.add(toDto(category));
        }
        
        return dtos;
    }
    
    public List<CategoryDTO> toDtoWithDepth(List<Object[]> categoriesWithDepth) {
        if (categoriesWithDepth == null) {
            return new ArrayList<>();
        }
        
        List<CategoryDTO> dtos = new ArrayList<>(categoriesWithDepth.size());
        for (Object[] row : categoriesWithDepth) {
            Category category = (Category) row[0];
            Integer depth = (Integer) row[1];
            
            CategoryDTO dto = toDto(category);
            dto.setDepth(depth);
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    public List<CategoryDTO> toDtoTree(List<Object[]> categoryTree) {
        if (categoryTree == null) {
            return new ArrayList<>();
        }
        
        // First pass: create all DTOs
        Map<Long, CategoryDTO> dtoMap = new HashMap<>();
        for (Object[] row : categoryTree) {
            Category category = (Category) row[0];
            Category parent = (Category) row[1];
            Integer depth = (Integer) row[2];
            
            CategoryDTO dto = dtoMap.computeIfAbsent(category.id, k -> {
                CategoryDTO newDto = new CategoryDTO();
                newDto.setId(category.id);
                newDto.setName(category.name);
                newDto.setChildren(new ArrayList<>());
                return newDto;
            });
            
            dto.setDepth(depth);
            
            if (parent != null) {
                dto.setParentId(parent.id);
                dto.setParentName(parent.name);
            }
        }
        
        // Second pass: build the tree
        List<CategoryDTO> rootCategories = new ArrayList<>();
        for (CategoryDTO dto : dtoMap.values()) {
            if (dto.getParentId() == null) {
                rootCategories.add(dto);
            } else {
                CategoryDTO parentDto = dtoMap.get(dto.getParentId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            }
        }
        
        return rootCategories;
    }
}