package org.workswap.listing.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.CatalogFilterDTO;
import org.workswap.listing.dto.FullListingDTO;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.sso.security.dto.UserAuthData;

public interface ListingQueryService {

    Listing getListingById(Long listingId);

    boolean isFavorite(UserAuthData authData, Long listingId);

    Page<ShortListingDTO> getSortedCatalog(
        Optional<UserAuthData> optAuthData, 
        CatalogFilterDTO filters, 
        String locale
    );
    
    List<FullListingDTO> getListingDtosByUser(Long userId, String locale);
    List<FullListingDTO> getRecentListings(int amount, String locale);
    List<FullListingDTO> getOwnListingsByUser(UserAuthData authData, String locale);
    Page<FullListingDTO> getListingsPage(int page, int amount, String sortParam, String locale);
    List<ShortListingDTO> getFavorites(UserAuthData authData, String locale);
    List<FullListingDTO> getDrafts(UserAuthData authData, String locale);
    Map<String, ListingTranslationDTO> getTranslations(Long id);
    List<ImageDTO> getImages(Long id);

    ListingDTO.Page getListingPage(Optional<UserAuthData> authData, String token, Long id, String locale);
    ShortListingDTO getCatalogListing(Long listingId, UserAuthData authData, String locale);
    FullListingDTO getListingDTO(Long listingId, UserAuthData authData, String locale);

    String getListingToken(UserAuthData authData, Long listingId);
}