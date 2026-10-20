package org.workswap.listing.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.workswap.category.datasource.Category;
import org.workswap.listing.datasource.model.Image;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.repository.ImageRepository;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.dto.CatalogFilterDTO;
import org.workswap.listing.dto.FullListingDTO;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.ProductType;
import org.workswap.listing.enums.ServiceType;
import org.workswap.listing.services.ListingMappingService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.listing.services.category.query.ProductCategoryQueryService;
import org.workswap.listing.services.category.query.ServiceCategoryQueryService;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.dto.ShortUserProfileDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
@RequiredArgsConstructor
@Profile("server")
public class ListingQueryServiceImpl implements ListingQueryService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    private final ServiceCategoryQueryService serviceCategoryQueryService;
    private final ProductCategoryQueryService productCategoryQueryService;
    private final ListingMappingService mappingService;
    private final ListingTranslationRepository translationRepository;
    private final SecurityFilterService securityFilterService;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageRepository imageRepository;

    public boolean isFavorite(UserAuthData authData, Long listingId) {
        return listingRepository.existsFavoriteListing(authData.sub(), listingId);
    }

    public Page<FullListingDTO> getListingsPage(int page, int amount, String sortParam, String locale) {

        if (sortParam == null || sortParam.length() == 0) sortParam = "createdAt";

        Pageable pageable = PageRequest.of(page, amount, Sort.by(sortParam).descending());
        Page<Listing> listings = listingRepository.findAllByTemporaryFalseOrderByCreatedAtDesc(pageable);

        List<FullListingDTO> dtos = mappingService.toDTOList(listings.getContent(), locale);

        return new PageImpl<FullListingDTO>(
            dtos,
            pageable,
            listings.getTotalElements()
        );
    }

    public List<FullListingDTO> getRecentListings(int amount, String locale) {
        
        return getListingsPage(0, amount, "createdAt", locale).getContent();
    }

    public Listing getListingById(Long listingId) {

        Objects.requireNonNull(listingId, "listingId must not be null");

        return Objects.requireNonNull(
            listingRepository.findById(listingId)
                .orElseThrow(() ->
                    new EntityNotFoundException("Объявление не найдено с ID = " + listingId)
                )
        );
    }

    public Page<ShortListingDTO> getSortedCatalog(
        Optional<UserAuthData> optAuthData, 
        CatalogFilterDTO filters, 
        String locale
    ) {

        List<String> languages = new ArrayList<>();

        log.debug("Язык: {}", locale);

        if (filters.translationsFilter() && optAuthData.isPresent()) {
            userRepository.findLanguagesByUserSub(optAuthData.get().sub());

            if (!languages.contains(locale)) {
                languages.add(locale);
            }
        } else {
            languages.addAll(LanguageUtils.SUPPORTED_LANGUAGES);
        }

        long t0 = System.currentTimeMillis();

        List<? extends Category> categories = Collections.emptyList();

        ListingPublicType publicType = null;

        ListingType listingType = null;
        ServiceType serviceType = null;
        ProductType productType = null;

        if (filters.type() != null) {
            publicType = ListingPublicType.valueOf(filters.type());
            listingType = publicType.getListingType();

            log.debug("Тип объявления: {} {}", publicType, listingType);

            switch (listingType) {
                case SERVICE:
                    categories = serviceCategoryQueryService.getAllDescendantsById(filters.categoryId());
                    serviceType = publicType.getServiceType();
                    break;
                case PRODUCT:
                    categories = productCategoryQueryService.getAllDescendantsById(filters.categoryId());
                    productType = publicType.getProductType();
                    break;
                case EVENT:
                    // ничего
                    break;
            }
        }

        log.debug("⏱️ categories: {} ms", System.currentTimeMillis() - t0);

        log.debug("Все параметры переформатированы, делаем запрос в бд");

        log.debug("Языки: {}", languages);

        t0 = System.currentTimeMillis();

        PageRequest pageable = PageRequest.of(filters.page(), 39);
        Page<ShortListingDTO> listings = listingRepository.findListings(
            categories,
            filters.location(),
            filters.searchQuery(),
            filters.hasReviews(),
            filters.translationsFilter(),
            languages,
            listingType,
            serviceType,
            productType,
            filters.sortBy(),
            pageable,
            optAuthData
        );

        List<Long> ids = listings.stream().map(l -> l.id()).toList();
        List<ListingTranslation> translations = translationRepository.findByListingIdsAndLanguages(ids, languages);

        Map<Long, ListingTranslation> translationByListing =
            translations.stream()
                .collect(Collectors.toMap(
                    t -> t.getListing().getId(),
                    t -> t
                ));

        Page<ShortListingDTO> mapped = listings.map(listing -> 
            ShortListingDTO.updateTranslation(
                listing, 
                translationByListing.get(
                    listing.id()
                )
            )
        );


        log.debug("⏱️ DB query: {} ms", System.currentTimeMillis() - t0);
        log.debug("Пришёл запрос из бд");

        return mapped;
    }

    public List<FullListingDTO> getListingDtosByUser(Long userId, String locale) {

        List<Listing> listings = listingRepository.findByAuthorIdAndActiveTrue(userId);
        return mappingService.toDTOList(listings, locale);
    }

    public List<FullListingDTO> getOwnListingsByUser(UserAuthData authData, String locale) {
        List<Listing> listings = listingRepository.findByAuthorSubWithAllDetails(authData.sub());
        return mappingService.toDTOList(listings, locale);
    }

    public List<ShortListingDTO> getFavorites(UserAuthData authData, String locale) {
        return listingRepository.findLikedListings(authData.sub(), locale);
    }

    public Map<String, ListingTranslationDTO> getTranslations(Long listingId) {
        if (listingId == null) {
            throw new IllegalStateException("ID объявления отсутствует");
        }

        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        
        return listing.getTranslations().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ListingTranslationDTO(
                    entry.getValue().getTitle(), 
                    entry.getValue().getDescription()
                )
            ));
    }

    public List<ImageDTO> getImages(Long listingId) {

        List<Image> images = imageRepository.findByListingId(listingId);

        return ImageDTO.ofList(images);
    }

    public String getListingToken(UserAuthData authData, Long listingId) {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = getListingById(listingId);
        return listing.getAccessToken();
    }

    public List<FullListingDTO> getDrafts(UserAuthData authData, String locale) {

        List<Listing> listings = listingRepository.findByAuthorSubAndTemporary(authData.sub(), true);

        return mappingService.toDTOList(listings, locale);
    }

    public ListingDTO.Page getListingPage(Optional<UserAuthData> optAuthData, String token, Long listingId, String locale) {

        if (listingId == null) {
            throw new IllegalStateException("ID объявления отсутствует");
        }

        Listing listing = getListingById(listingId);
        ListingTranslation translation = translationRepository.findBestTranslation(listingId, locale);

        securityFilterService.listingGetFilter(optAuthData.get(), listing, token);

        mappingService.setListingCategoryMeta(listing);

        ShortUserProfileDTO author = ShortUserProfileDTO.ofUser(listing.getAuthor());
        List<ImageDTO> images = ImageDTO.ofList(listing.getImages());

        if (optAuthData.isPresent()) {
            UserAuthData authData = optAuthData.get();
            
            eventPublisher.publishEvent(
                new ListingViewedEvent(
                    authData.sub(), 
                    listingId, 
                    authData.status().equals(UserStatus.TEMP), 
                    LocalDateTime.now()
                ));
        }

        FullListingDTO listingDto = FullListingDTO.ofListingForListingPage(listing, translation);

        return new ListingDTO.Page(
            listingDto,
            author,
            images
        );
    }

    public ShortListingDTO getCatalogListing(Long listingId, UserAuthData authData, String locale) {

        Listing listing = getListingById(listingId);
        securityFilterService.listingGetFilter(authData, listing, null);

        return mappingService.toShortDTO(listing, locale);
    }

    public FullListingDTO getListingDTO(Long listingId, UserAuthData authData, String locale) {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = getListingById(listingId);
        return mappingService.toDTO(listing, locale);
    }
}