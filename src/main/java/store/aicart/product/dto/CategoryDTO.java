package store.aicart.product.dto;

import java.util.List;

public class CategoryDTO {
    private Long id;
    private String name;
    private List<CategoryDTO> children; // Update to be a list of CategoryDTO, not Long

    // No-argument constructor
    public CategoryDTO() {
    }

    // Constructor with parameters
    public CategoryDTO(Long id, String name, List<CategoryDTO> children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }

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

    public List<CategoryDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryDTO> children) {
        this.children = children;
    }
}