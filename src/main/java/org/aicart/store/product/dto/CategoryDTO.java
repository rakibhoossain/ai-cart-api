package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;
import java.util.stream.Collectors;

class CategoryType {
    private Long id;
    private String name;
    private Integer depth;
    @JsonProperty("category_id")
    private Long categoryId;

    CategoryType() {}

    public CategoryType(Long id, String name, Integer depth, Long categoryId) {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.categoryId = categoryId;
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

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

public class CategoryDTO {
    private Long id;
    private String name;
    private CategoryDTO children;

    // Getters and Setters
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

    public CategoryDTO getChildren() {
        return children;
    }

    public void setChildren(CategoryDTO children) {
        this.children = children;
    }

    // Method to parse the flat JSON array into a hierarchy using recursion
    public static List<CategoryDTO> parseJsonToHierarchy(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<CategoryType> flatCategories = mapper.readValue(json, new TypeReference<>() {});

        Map<Long, List<CategoryType>> categoryGroups = flatCategories.stream()
                .collect(Collectors.groupingBy(CategoryType::getCategoryId));

        List<CategoryDTO> rootCategories = new ArrayList<>();

        categoryGroups.forEach((id, categoryList) -> {
            categoryList.sort((a, b) -> Integer.compare(b.getDepth(), a.getDepth()));
            CategoryDTO category = new CategoryDTO();

            CategoryType firstItem = categoryList.removeFirst();
            category.setId(firstItem.getId());
            category.setName(firstItem.getName());

            if(!categoryList.isEmpty()) {
                addChildrenRecursively(category, categoryList);
            }

            rootCategories.add(category);
        });


        return rootCategories;
    }

    // Recursive method to assign children to parents
    private static void addChildrenRecursively(CategoryDTO parent, List<CategoryType> categoryList) {

        CategoryType firstItem = categoryList.removeFirst();

        CategoryDTO category = new CategoryDTO();
        category.setId(firstItem.getId());
        category.setName(firstItem.getName());

        parent.children = category;

        if(!categoryList.isEmpty()) {
            addChildrenRecursively(parent.children, categoryList);
        }
    }
}
