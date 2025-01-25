package org.aicart.media;

import io.minio.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.media.dto.FileRequestDTO;
import org.aicart.media.dto.MediaDTO;
import org.aicart.media.entity.FileStorage;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

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

        // Explicitly register TwelveMonkeys plugins
        ImageIO.scanForPlugins();

        // Get object from source bucket
        GetObjectArgs getRequest = buildGetRequest(fileRequestDTO.getObjectKey());

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
        BufferedImage originalImage = ImageIO.read(inputStream);
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
        mediaDTO.setAltText(fileRequestDTO.getFileName());
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
}
