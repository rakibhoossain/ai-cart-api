package org.aicart.media;

import io.minio.*;
import com.sksamuel.scrimage.ImmutableImage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.media.dto.FileRequestDTO;
import org.aicart.media.dto.MediaDTO;
import org.aicart.media.dto.MediaFileDTO;
import org.aicart.media.dto.MediaListResponse;
import org.aicart.media.dto.MediaUpdateDTO;
import org.aicart.media.entity.FileStorage;
import org.aicart.media.mapper.MediaFileMapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class MediaService {

    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;

    @ConfigProperty(name = "quarkus.minio.url")
    String minioUrl;

    @Inject
    MinioClient minioClient;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    ImageService imageService;


    public FileStorage store(FileRequestDTO fileRequestDTO) throws Exception {

        // Get object from source bucket
        GetObjectArgs getRequest = buildGetRequest(fileRequestDTO.getObjectKey());


        System.out.println(fileRequestDTO.getObjectKey());

        try (InputStream inputStream = minioClient.getObject(getRequest) // Waits for the result in a blocking fashion
        ) {

            MediaDTO mediaDTO = processAndUploadImage(fileRequestDTO, inputStream);
            removeObject(fileRequestDTO.getObjectKey());

            return storeMedia(mediaDTO);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Transactional
    public FileStorage storeMedia(MediaDTO mediaDTO) {
        FileStorage file = new FileStorage();
        file.fileName = mediaDTO.getFileName();
        file.fileType = mediaDTO.getFileType();
        file.mimeType = mediaDTO.getMimeType();
        file.originalUrl = mediaDTO.getOriginalUrl();
        file.thumbnailUrl = mediaDTO.getThumbnailUrl();
        file.mediumUrl = mediaDTO.getMediumUrl();
        file.fileSize = mediaDTO.getFileSize();
        file.width = mediaDTO.getWidth();
        file.height = mediaDTO.getHeight();
        file.altText = mediaDTO.getAltText();
        file.storageLocation = mediaDTO.getStorageLocation();

        file.persist();

        return file;
    }


    public MediaDTO processAndUploadImage(FileRequestDTO fileRequestDTO, InputStream inputStream) throws Exception {

        // Use ImmutableImage to read the InputStream
        ImmutableImage immutableImage = ImmutableImage.loader().fromStream(inputStream);

        // BufferedImage
        BufferedImage originalImage = immutableImage.awt();

        String baseFileName = UUID.randomUUID().toString();

        byte[] outputByteStream = imageService.resizeAndConvertToWebp(originalImage, originalImage.getWidth(), originalImage.getHeight());

        byte[] outputByteStreamThumbnail = imageService.resizeAndConvertToWebp(originalImage, 450);

        byte[] outputByteStreamSmall =imageService.resizeAndConvertToWebp(originalImage, 150);

        // Original WebP
        String originalUrl = uploadWebP(outputByteStream, baseFileName);

        // Thumbnail
        String thumbnailUrl = uploadWebP(outputByteStreamThumbnail, baseFileName + "_450");

        // Medium image
        String mediumUrl = uploadWebP(outputByteStreamSmall, baseFileName + "_150");

        MediaDTO mediaDTO = new MediaDTO();
        mediaDTO.setFileName(fileRequestDTO.getFileName());
        mediaDTO.setFileType("image");
        mediaDTO.setMimeType(fileRequestDTO.getMimeType());
        mediaDTO.setOriginalUrl(originalUrl);
        mediaDTO.setThumbnailUrl(thumbnailUrl);
        mediaDTO.setMediumUrl(mediumUrl);
        mediaDTO.setFileSize((long) outputByteStream.length);
        mediaDTO.setWidth(originalImage.getWidth());
        mediaDTO.setHeight(originalImage.getHeight());
        mediaDTO.setAltText(fileRequestDTO.getAltText() != null ? fileRequestDTO.getAltText() : fileRequestDTO.getFileName());
        mediaDTO.setStorageLocation(minioUrl + "/" + bucketName);

        return mediaDTO;
    }


    private String uploadWebP(byte[] outputByteStream, String objectName) throws Exception {
        // Convert to InputStream for MinIO upload
        InputStream inputStream = new ByteArrayInputStream(outputByteStream);
        long size = outputByteStream.length;

        String fullObjectName = "products/" + objectName + ".webp";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fullObjectName)
                        .stream(inputStream, size, -1)
                        .contentType("image/webp")
                        .build());

        return fullObjectName;
    }

    private GetObjectArgs buildGetRequest(String objectKey) {
        return GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .build();
    }

    private void removeObject(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // New CRUD methods for media management
    public MediaListResponse findAllWithFilters(String search, String fileType, int page, int size, String sortBy, String order) {
        List<FileStorage> files = mediaRepository.findWithFilters(search, fileType, page, size, sortBy, order);
        long total = mediaRepository.countWithFilters(search, fileType);

        List<MediaFileDTO> fileDTOs = files.stream()
                .map(MediaFileMapper::toDto)
                .collect(Collectors.toList());

        return new MediaListResponse(fileDTOs, total, page, size);
    }

    public MediaFileDTO findById(Long id) {
        FileStorage file = mediaRepository.findById(id);
        return MediaFileMapper.toDto(file);
    }

    @Transactional
    public MediaFileDTO updateMedia(Long id, MediaUpdateDTO updateDTO) {
        FileStorage file = mediaRepository.findById(id);
        if (file == null) {
            throw new RuntimeException("Media file not found with id: " + id);
        }

        if (updateDTO.getFileName() != null) {
            file.fileName = updateDTO.getFileName();
        }
        if (updateDTO.getAltText() != null) {
            file.altText = updateDTO.getAltText();
        }
        if (updateDTO.getMetadata() != null) {
            file.metadata = updateDTO.getMetadata();
        }

        file.persist();
        return MediaFileMapper.toDto(file);
    }

    @Transactional
    public boolean deleteMedia(Long id) {
        FileStorage file = mediaRepository.findById(id);
        if (file == null) {
            return false;
        }

        // TODO: Also delete files from MinIO storage
        // removeObject(file.originalUrl);
        // removeObject(file.thumbnailUrl);
        // removeObject(file.mediumUrl);

        file.delete();
        return true;
    }
}
