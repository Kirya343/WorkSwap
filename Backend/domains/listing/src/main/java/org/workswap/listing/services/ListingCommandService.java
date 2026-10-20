package org.workswap.listing.services;

import java.util.Map;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.enums.ListingTranslateType;
import org.workswap.sso.security.dto.UserAuthData;

public interface ListingCommandService {

    Listing create(UserAuthData authData, String type);
    void delete(UserAuthData authData, Long listingId);
    void publish(UserAuthData authData, Long listingId);
    void addListingToFavorite(UserAuthData authData, Long listingId);
    void removeListingFromFavorite(UserAuthData authData, Long listingId);

    void modifyListingParam(
        UserAuthData authData, 
        Long listingId, 
        ListingDTO.Update updates
    ) throws AccessDeniedException;

    Set<String> updateListingTranslations(
        UserAuthData authData,
        Long listingId, 
        Map<String, ListingTranslationDTO> translationsMap,
        ListingTranslateType translateType
    );

    ListingTranslationDTO autoTranslateListing(
        UserAuthData authData, 
        Long listingId, 
        String lang, 
        String preferedRefLang);
}
