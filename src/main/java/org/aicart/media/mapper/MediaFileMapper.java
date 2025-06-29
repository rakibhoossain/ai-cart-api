package org.aicart.media.mapper;

import org.aicart.media.dto.MediaFileDTO;
import org.aicart.media.entity.FileStorage;

public class MediaFileMapper {

    public static MediaFileDTO toDto(FileStorage fileStorage) {
        if (fileStorage == null) {
            return null;
        }

        return new MediaFileDTO(
            fileStorage.id,
            fileStorage.fileName,
            fileStorage.originalUrl,
            fileStorage.thumbnailUrl,
            fileStorage.mediumUrl,
            fileStorage.fileType,
            fileStorage.mimeType,
            fileStorage.fileSize,
            fileStorage.width,
            fileStorage.height,
            fileStorage.altText,
            fileStorage.storageLocation,
            fileStorage.metadata,
            fileStorage.createdAt,
            fileStorage.updatedAt
        );
    }

    public static FileStorage toEntity(MediaFileDTO dto) {
        if (dto == null) {
            return null;
        }

        FileStorage fileStorage = new FileStorage();
        fileStorage.id = dto.getId();
        fileStorage.fileName = dto.getFileName();
        fileStorage.originalUrl = dto.getOriginalUrl();
        fileStorage.thumbnailUrl = dto.getThumbnailUrl();
        fileStorage.mediumUrl = dto.getMediumUrl();
        fileStorage.fileType = dto.getFileType();
        fileStorage.mimeType = dto.getMimeType();
        fileStorage.fileSize = dto.getFileSize();
        fileStorage.width = dto.getWidth();
        fileStorage.height = dto.getHeight();
        fileStorage.altText = dto.getAltText();
        fileStorage.storageLocation = dto.getStorageLocation();
        fileStorage.metadata = dto.getMetadata();
        fileStorage.createdAt = dto.getCreatedAt();
        fileStorage.updatedAt = dto.getUpdatedAt();

        return fileStorage;
    }
}
