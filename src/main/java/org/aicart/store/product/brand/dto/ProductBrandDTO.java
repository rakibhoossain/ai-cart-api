package org.aicart.store.product.brand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class ProductBrandDTO {
    private Long id;

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 255, message = "Brand name must be between 2 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @URL(message = "Logo must be a valid URL")
    @Size(max = 500, message = "Logo URL must be less than 500 characters")
    private String logo;

    @URL(message = "Website must be a valid URL")
    @Size(max = 500, message = "Website URL must be less than 500 characters")
    private String website;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductBrandDTO() {
    }

    public ProductBrandDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductBrandDTO(Long id, String name, String description, String logo, String website, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.website = website;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}