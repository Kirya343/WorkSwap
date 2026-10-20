package org.workswap.listing.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.model.types.ProductSettings;
import org.workswap.listing.datasource.model.types.ServiceSettings;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.dto.FullListingDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.services.ListingMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class ListingMappingServiceImpl implements ListingMappingService {

    private final ListingTranslationRepository translationRepository;

    @Transactional
    public FullListingDTO toDTO(Listing listing, String locale) {

        ListingTranslation translation = translationRepository.findBestTranslation(
            listing.getId(), locale);

        return FullListingDTO.ofListing(listing, translation);
    }

    @Transactional
    public ShortListingDTO toShortDTO(Listing listing, String locale) {

        ListingTranslation translation = translationRepository.findBestTranslation(
            listing.getId(), locale);

        return ShortListingDTO.ofListing(listing, translation);
    }

    public Map<Long, ListingTranslation> getBestListingsTranslations(Collection<Listing> listings, String locale) {
        List<Long> ids = listings.stream()
            .map(Listing::getId)
            .toList();

        return translationRepository
            .findBestTranslations(ids, locale)
            .stream()
            .collect(Collectors.toMap(
                ListingTranslation::getListingId,
                Function.identity()
            ));
    }

    public List<FullListingDTO> toDTOList(Collection<Listing> listings, String locale) {

        Map<Long, ListingTranslation> translations = getBestListingsTranslations(listings, locale);

        return listings.stream()
            .map(listing -> FullListingDTO.ofListing(
                listing, 
                translations.get(listing.getId())
            ))
            .toList();
    }

    public List<ShortListingDTO> toShortDTOList(Collection<Listing> listings, String locale) {
        Map<Long, ListingTranslation> translations = getBestListingsTranslations(listings, locale);

        return listings.stream()
            .map(listing -> ShortListingDTO.ofListing(
                listing, 
                translations.get(listing.getId())
            ))
            .toList();
    }

    public void setListingCategoryMeta(Listing listing) {
        ListingType type = listing.getType();
        switch (type) {
            case SERVICE:
                ServiceSettings sSettings = listing.getServiceSettings();
                if (sSettings != null) {
                    ServiceCategory category = sSettings.getCategory();
                    if (category != null) {
                        listing.setCategoryName(category.getName());
                        listing.setCategoryId(category.getId());
                    }
                }
                break;

            case PRODUCT:
                ProductSettings pSettings = listing.getProductSettings();
                if (pSettings != null) {
                    ProductCategory category = pSettings.getCategory();
                    if (category != null) {
                        listing.setCategoryName(category.getName());
                        listing.setCategoryId(category.getId());
                    }
                }
                break;

            default: 
                break;
        }
    }
}
