package org.aicart.media.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FileRequestDTO {

    @NotBlank(message = "Object Key is required")
    private String objectKey;

    @NotNull(message = "File size is required")
    private long fileSize;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Mime type is required")
    private String mimeType;

    private String altText;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String fileType) {
        this.mimeType = fileType;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
