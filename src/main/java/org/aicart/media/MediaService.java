package org.aicart.media;

import io.minio.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.media.dto.FileRequestDTO;
import org.aicart.media.entity.FileStorage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@ApplicationScoped
public class MediaService {

    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;

    @Inject
    MinioClient minioClient;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    ImageService imageService;


    @Transactional
    public FileStorage store(FileRequestDTO fileRequestDTO) {

        // Explicitly register TwelveMonkeys plugins
        ImageIO.scanForPlugins();

        // Get object from source bucket
        GetObjectArgs getRequest = buildGetRequest("products/c9d1045f-0221-4d22-8d87-8cd3c96060f9-Screenshot_2025-01-14_at_2.11.14_PM.png");

        System.out.println(getRequest);

        try (InputStream inputStream = minioClient.getObject(getRequest) // Waits for the result in a blocking fashion
        ) {

            processAndUploadImage(inputStream);
//            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//            System.out.println(content);

        } catch (Exception e) {
            System.out.println(e);
        }

//        System.out.println(s3Object);

        FileStorage file = new FileStorage();
        file.originalUrl = fileRequestDTO.getUrl();
        file.fileSize = fileRequestDTO.getFileSize();
        file.fileName = fileRequestDTO.getFileName();
        file.fileType = "image";
        file.mimeType = fileRequestDTO.getMimeType();

        file.storageLocation = "minio";

        file.persist();

        return file;
    }


    public void processAndUploadImage(InputStream inputStream) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        String baseFileName = UUID.randomUUID().toString();

        // Original WebP
        uploadWebP(originalImage, baseFileName + "_original", originalImage.getWidth(), originalImage.getHeight());

//        // Thumbnail
        uploadWebP(originalImage, baseFileName + "_thumbnail", 150);
//
//        // Medium image
        uploadWebP(originalImage, baseFileName + "_medium", 450);
    }

    private void uploadWebP(BufferedImage originalImage, String objectName, int targetWidth) throws IOException {
        uploadWebP(originalImage, objectName,
                Math.min(originalImage.getWidth(), targetWidth),
                (int)((double)targetWidth / originalImage.getWidth() * originalImage.getHeight())
        );
    }

    private void uploadWebP(BufferedImage originalImage, String objectName, int width, int height) {

        // Upload with detailed logging
        try {
            byte[] outputByteStream = imageService.resizeAndConvertToWebp(originalImage, width, height);

            // Convert to InputStream for MinIO upload
            InputStream inputStream = new ByteArrayInputStream(outputByteStream);
            long size = outputByteStream.length;

            // Create local directory if not exists
            Path localDir = Paths.get("./temp_images");
            Files.createDirectories(localDir);

            // Generate local file path
            String fullFileName = objectName + ".png";
            Path localFilePath = localDir.resolve(fullFileName);


            Files.write(localFilePath, outputByteStream);

            String fullObjectName = "products/" + objectName + ".png";


            System.out.println("size");
            System.out.println(size);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullObjectName)
                            .stream(inputStream, size, -1)
                            .contentType("image/png")
                            .build());

            System.out.println("Successfully uploaded: " + fullObjectName);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }


//    private void uploadWebP(BufferedImage originalImage, String objectName, int width, int height) {
//
//        // Upload with detailed logging
//        try {
//
//            System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
//
//            // Resize and convert
//            BufferedImage resizedImage = Scalr.resize(
//                    originalImage,
//                    Scalr.Method.QUALITY,
//                    Scalr.Mode.FIT_EXACT,
//                    width,
//                    height
//            );
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            ImageIO.write(resizedImage, "png", outputStream);
//
//            // Convert to InputStream for MinIO upload
//            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
//            long size = outputStream.size();
//
//            // Create local directory if not exists
//            Path localDir = Paths.get("./temp_images");
//            Files.createDirectories(localDir);
//
//            // Generate local file path
//            String fullFileName = objectName + ".png";
//            Path localFilePath = localDir.resolve(fullFileName);
//
//
//            Files.write(localFilePath, outputStream.toByteArray());
//
//            String fullObjectName = "products/" + objectName + ".png";
//
//
//            System.out.println("size");
//            System.out.println(size);
//
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                    .bucket(bucketName)
//                            .object(fullObjectName)
//                            .stream(inputStream, size, -1)
//                            .contentType("image/png")
//                            .build());
//
//            System.out.println("Successfully uploaded: " + fullObjectName);
//        } catch (Exception e) {
//            System.err.println(e);
//            e.printStackTrace();
//        }
//    }

    private GetObjectArgs buildGetRequest(String objectKey) {
        return GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .build();
    }
}
