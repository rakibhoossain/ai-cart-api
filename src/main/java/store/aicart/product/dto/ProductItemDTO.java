package store.aicart.product.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import io.quarkus.logging.Log;

public class ProductItemDTO {
    private Long id;
    private String name;
    private List<CategoryDTO> categories;

    // Constructor
    public ProductItemDTO(Long id, String name, String categoriesJson) {
        this.id = id;
        this.name = name;

        try {
            this.categories = CategoryDTO.parseJsonToHierarchy(categoriesJson);
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            Log.info(categoriesJson);
//            this.categories = objectMapper.readValue(categoriesJson, new TypeReference<>() {
//
//            });

        } catch (Exception e) {
            Log.warn(e.getMessage());
            this.categories = Collections.emptyList();
        }
    }

    public ProductItemDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }
}
