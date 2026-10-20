package org.workswap.storage;

import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class ImageStorageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageStorageService.class);
    private final S3StorageService s3StorageService;
    
    public String storeImage(String directory, String prefix, MultipartFile image) {

        if (image.isEmpty()) {
            throw new RuntimeException("Empty file");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only images allowed");
        }

        String extension = resolveExtension(contentType);
        String fileName = prefix + "_" + UUID.randomUUID() + extension;

        try (InputStream input = image.getInputStream()) {

            s3StorageService.upload(
                input,
                fileName,
                image.getSize(),
                contentType,
                directory
            );

        } catch (Exception e) {
            logger.error("Ошибка загрузки изображения в s3, папка: {}", directory, e);
            throw new RuntimeException("Image upload failed");
        }

        return fileName;
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> "";
        };
    }
}
