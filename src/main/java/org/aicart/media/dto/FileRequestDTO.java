package org.aicart.media.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FileRequestDTO {

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "File size is required")
    private long fileSize;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Mime type is required")
    private String mimeType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
