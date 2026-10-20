package org.workswap.listing.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.workswap.listing.dto.CatalogFilterDTO;
import org.workswap.listing.dto.FullListingDTO;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingTranslateType;
import org.workswap.listing.services.ListingCommandService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.ListingStorageService;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.annotations.parameters.OptionalAuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/listing")
public class ListingsController {

    private final ListingQueryService listingQueryService;
    private final ListingCommandService listingCommandService;
    private final ListingStorageService listingStorageService;

    @GetMapping("/{listingId}")
    @RequiredPermission("GET_LISTING_BY_ID")
    public FullListingDTO getListing(
            @AuthUser UserAuthData authData, 
            @PathVariable Long listingId, 
            @RequestParam(required = false) String token,
            @RequestParam String locale
    ) {
        return listingQueryService.getListingDTO(listingId, authData, locale);
    }

    @PutMapping
    @RequiredPermission("CREATE_LISTING")
    public Long createListing(
            @RequestParam String type,
            @AuthUser UserAuthData authData
    ) {
        return listingCommandService.create(authData, type).getId();
    }

    @DeleteMapping("/{listingId}")
    @RequiredPermission("DELETE_LISTING")
    public void deleteListing(
            @PathVariable Long listingId, 
            @AuthUser UserAuthData authData
    ) {
        listingCommandService.delete(authData, listingId);
    }

    @GetMapping("/{listingId}/page")
    @RequiredPermission("GET_LISTING_BY_ID")
    public ListingDTO.Page getListingPage(
            @OptionalAuthUser Optional<UserAuthData> authData, 
            @PathVariable Long listingId, 
            @RequestParam(required = false) String token,
            @RequestParam String locale
    ) {
        return listingQueryService.getListingPage(authData, token, listingId, locale);
    }

    @PostMapping("/catalog") 
    @PublicEndpoint
    public Page<ShortListingDTO> getSortedCatalog(
            @RequestBody CatalogFilterDTO filters,
            @RequestParam String locale,
            @OptionalAuthUser Optional<UserAuthData> authData
    ) {
        return listingQueryService.getSortedCatalog(authData, filters, locale);
    }

    @GetMapping("/drafts")
    @RequiredPermission("VIEW_LISTINGS_DRAFTS")
    public List<FullListingDTO> getDraftListings(
            @AuthUser UserAuthData authData, 
            @RequestParam String locale
    ) {
        return listingQueryService.getDrafts(authData, locale);
    }

    @PostMapping("/{listingId}/favorite")
    @RequiredPermission("FAVORITE_LISTING")
    public void addFavorite(
            @PathVariable Long listingId, 
            @AuthUser UserAuthData authData
    ) {
        listingCommandService.addListingToFavorite(authData, listingId);
    }

    @DeleteMapping("/{listingId}/favorite")
    @RequiredPermission("FAVORITE_LISTING")
    public void removeFavorite(
            @PathVariable Long listingId, 
            @AuthUser UserAuthData authData
    ) {
        listingCommandService.removeListingFromFavorite(authData, listingId);
    }

    @GetMapping("/{listingId}/favorite")
    @RequiredPermission("CHECK_FAVORITE_LISTING")
    public boolean isFavorite(
            @PathVariable Long listingId, 
            @AuthUser UserAuthData authData
    ) {
        return listingQueryService.isFavorite(authData, listingId);
    }

    @PatchMapping("/{listingId}/publish")
    @RequiredPermission("PUBLISH_LISTING")
    public void publishListing(
            @PathVariable Long listingId, 
            @AuthUser UserAuthData authData
    ) {
        listingCommandService.publish(authData, listingId);
    }

    @GetMapping("/page")
    @RequiredPermission("GET_LISTINGS_LIST")
    public Page<FullListingDTO> getListingsPage(
            @RequestParam int page, 
            @RequestParam int amount, 
            @RequestParam String sortParam,
            @RequestParam String locale
    ) {
        return listingQueryService.getListingsPage(page, amount, sortParam, locale);
    }

    @GetMapping("/recent")
    @RequiredPermission("GET_RECENT_LISTINGS")
    public List<FullListingDTO> getRecentListings(
            @RequestParam int amount,
            @RequestParam String locale
    ) {
        return listingQueryService.getRecentListings(amount, locale);
    }

    @GetMapping("/my-listings")
    @RequiredPermission("GET_OWN_LISTINGS")
    public List<FullListingDTO> getMyListings(
            @AuthUser UserAuthData authData, 
            @RequestParam String locale
    ) {
        return listingQueryService.getOwnListingsByUser(authData, locale);
    }

    @GetMapping("/by-user")
    @PublicEndpoint
    public List<FullListingDTO> getListingsByUser(
            @RequestParam Long userId, 
            @RequestParam String locale
    ) {
        return listingQueryService.getListingDtosByUser(userId, locale);
    }

    @GetMapping("/favorites")
    @RequiredPermission("GET_FAVORITES_LISTINGS")
    public List<ShortListingDTO> getFavorites(
            @AuthUser UserAuthData authData, 
            @RequestParam String locale
    ) {
        return listingQueryService.getFavorites(authData, locale);
    }

    @GetMapping("/{listingId}/images")
    @PublicEndpoint
    public List<ImageDTO> getImages(
            @PathVariable Long listingId
    ) {
        return listingQueryService.getImages(listingId);
    }

    @GetMapping("/{listingId}/translations")
    @PublicEndpoint
    public Map<String, ListingTranslationDTO> getTranslations(
            @PathVariable Long listingId
    ) {
        return listingQueryService.getTranslations(listingId);
    }

    @GetMapping("/{listingId}/token")
    @Authenticated
    public String getToken(
            @AuthUser UserAuthData authData,
            @PathVariable Long listingId
    ) {
        return listingQueryService.getListingToken(authData, listingId);
    }

    @PostMapping("/{listingId}/update")
    @RequiredPermission("UPDATE_LISTING")
    public void modifyListing(
            @AuthUser UserAuthData authData,
            @PathVariable Long listingId,
            @RequestBody ListingDTO.Update updates
    ) throws AccessDeniedException {
        listingCommandService.modifyListingParam(authData, listingId, updates);
    }

    @PostMapping("/{listingId}/modify/translations")
    @RequiredPermission("UPDATE_LISTING")
    public Set<String> updateListingTranslations(
            @AuthUser UserAuthData authData,
            @PathVariable Long listingId,
            @RequestBody Map<String, ListingTranslationDTO> translations
    ) throws AccessDeniedException {
        return listingCommandService.updateListingTranslations(authData, listingId, translations, ListingTranslateType.HAND_MATE);
    }

    @PostMapping("/{listingId}/image")
    @Authenticated
    public ImageDTO uploadListingImage(
            @RequestParam MultipartFile image,
            @PathVariable Long listingId,
            @AuthUser UserAuthData authData
    ) {
        return listingStorageService.uploadListingImage(image, listingId, authData);
    }

    @DeleteMapping("/{listingId}/image/{imageId}")
    @Authenticated
    public void deleteListingImage(
            @RequestParam Long imageId,
            @AuthUser UserAuthData authData
    ) {
        listingStorageService.deleteListingImage(imageId, authData);
    }

    @PostMapping("/{listingId}/auto-translate")
    @Authenticated
    public ListingTranslationDTO autoTranslateListing(
            @PathVariable Long listingId,
            @RequestParam String lang,
            @RequestParam(required = false) String preferedRefLang,
            @AuthUser UserAuthData authData
    ) {
        return listingCommandService.autoTranslateListing(authData, listingId, lang, preferedRefLang);
    }
}
