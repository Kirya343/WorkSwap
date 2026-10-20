package org.workswap.listing.services.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.listing.datasource.model.Image;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ImageRepository;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.services.ListingStorageService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.storage.ImageStorageService;
import org.workswap.storage.S3StorageService;
import org.workswap.storage.util.HashUtil;
import org.workswap.storage.util.ImageUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class ListingStorageServiceImpl implements ListingStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ListingStorageServiceImpl.class);

    private final ListingRepository listingRepository;
    private final ImageRepository imageRepository;
    private final SecurityFilterService securityFilterService;
    private final ImageStorageService imageStorageService;
    private final S3StorageService storageService;
    private final EntityManager entityManager;
    
    public ImageDTO uploadListingImage(MultipartFile file, Long listingId, UserAuthData authData) {

        securityFilterService.listingUpdateFilter(authData, listingId);

        try {
            String prefix = "image-for-listing_" + listingId;
            String imageKey = imageStorageService.storeImage("listing-images", prefix, file);

            logger.debug(imageKey);

            ImageUtil.Dimension size = ImageUtil.getImageSize(file);
            String hash = HashUtil.sha256(file);

            // 2. Сохраняем в БД
            Image image = new Image(
                imageKey,
                file.getContentType(),
                file.getSize(),
                size.width(),
                size.height(),
                hash,
                entityManager.getReference(Listing.class, listingId)
            );

            Image savedImage = imageRepository.save(image);

            logger.debug(savedImage.getId().toString());
            logger.debug(savedImage.getObjectKey());

            listingRepository.setImagePathIfEmpty(listingId, imageKey);

            return ImageDTO.ofImage(savedImage);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка загрузки изображения");
        }
    }

    public void deleteListingImage(Long imageId, UserAuthData authData) {
        Image image = imageRepository.findById(imageId).orElseThrow(
            () -> new EntityNotFoundException("Image by this Id does not exist")
        );
        Listing listing = image.getListing();
        String imageKey = image.getObjectKey();

        securityFilterService.listingUpdateFilter(authData, listing.getId());

        try {
            storageService.delete(imageKey);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления изображения объявления: " + imageKey);
        }

        if (listing.getImagePath().equals(URLDecoder.decode(imageKey, StandardCharsets.UTF_8))) {
            listing.setImagePath(null);
            listingRepository.save(listing);
        }
    }
}
