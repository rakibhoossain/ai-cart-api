package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ImageDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("relation_id")
    private Long relationId;

    @JsonProperty("original_url")
    private String originalUrl;

    @JsonProperty("medium_url")
    private String mediumUrl;

    @JsonProperty(value = "storage_location", access = JsonProperty.Access.WRITE_ONLY)
    private String storageLocation;

    @JsonProperty("score")
    private int score;

    @JsonIgnore
    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getOriginalUrl() {
        return storageLocation + "/" + originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getMediumUrl() {
        return storageLocation + "/" + mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    // Method to parse JSON into a list of ProductVariantDTO
    public static List<ImageDTO> parseJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // Parse and return the list of ProductVariantDTO
        return mapper.readValue(json, new TypeReference<List<ImageDTO>>() {});
    }
}
