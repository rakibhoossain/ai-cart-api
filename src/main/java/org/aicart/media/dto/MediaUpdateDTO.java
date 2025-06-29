package org.aicart.media.dto;

import jakarta.validation.constraints.Size;

public class MediaUpdateDTO {
    
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;
    
    @Size(max = 500, message = "Alt text must not exceed 500 characters")
    private String altText;
    
    private String metadata;

    // Constructors
    public MediaUpdateDTO() {}

    public MediaUpdateDTO(String fileName, String altText, String metadata) {
        this.fileName = fileName;
        this.altText = altText;
        this.metadata = metadata;
    }

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
