package org.workswap.listing.services;

import org.springframework.web.multipart.MultipartFile;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.sso.security.dto.UserAuthData;

public interface ListingStorageService {
    
    ImageDTO uploadListingImage(MultipartFile file, Long listingId, UserAuthData authData);
    void deleteListingImage(Long imageId, UserAuthData authData);
}
