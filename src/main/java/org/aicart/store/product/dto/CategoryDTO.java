package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aicart.store.product.entity.Category;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private Integer depth;
    private List<CategoryDTO> children;

    public CategoryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public List<CategoryDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryDTO> children) {
        this.children = children;
    }

    /**
     * Parse JSON string into a hierarchical list of CategoryDTO objects
     * @param json JSON string containing category data
     * @return List of root CategoryDTO objects with their children
     * @throws JsonProcessingException if JSON parsing fails
     */
    public static List<CategoryDTO> parseJsonToHierarchy(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        
        // First, parse the flat list of categories
        List<CategoryDTO> flatCategories = mapper.readValue(json, new TypeReference<List<CategoryDTO>>() {});
        
        // Create a map for quick lookup by ID
        Map<Long, CategoryDTO> categoryMap = new HashMap<>();
        for (CategoryDTO category : flatCategories) {
            // Ensure each category has a children list
            if (category.getChildren() == null) {
                category.setChildren(new ArrayList<>());
            }
            categoryMap.put(category.getId(), category);
        }
        
        // Build the hierarchy
        List<CategoryDTO> rootCategories = new ArrayList<>();
        for (CategoryDTO category : flatCategories) {
            if (category.getParentId() == null) {
                // This is a root category
                rootCategories.add(category);
            } else {
                // This is a child category, add it to its parent
                CategoryDTO parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(category);
                } else {
                    // Parent not found, treat as root
                    rootCategories.add(category);
                }
            }
        }
        
        return rootCategories;
    }

    /**
     * Parse JSON string into a flat list of CategoryDTO objects
     * @param json JSON string containing category data
     * @return Flat list of CategoryDTO objects
     * @throws JsonProcessingException if JSON parsing fails
     */
    public static List<CategoryDTO> parseJson(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<CategoryDTO>>() {});
    }
}
