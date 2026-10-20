package org.workswap.listing.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.listing.datasource.model.Image;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.repository.ImageRepository;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.datasource.repository.category.ProductCategoryRepository;
import org.workswap.listing.datasource.repository.category.ServiceCategoryRepository;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingTranslateType;
import org.workswap.listing.services.ListingCommandService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.listing.services.translations.DeepLTranslationService;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.shared.events.listing.ListingDeletedEvent;
import org.workswap.shared.locale.LanguageMapper;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile({"server"})
public class ListingCommandServiceImpl implements ListingCommandService {

    private final ListingRepository listingRepository;
    private final LocationRepository locationRepository;
    private final ImageRepository imageRepository;

    private final EntityManager entityManager;
    private final SecurityFilterService securityFilterService;
    private final ListingQueryService listingQueryService;
    private final ProductCategoryRepository productCategoryRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ListingTranslationRepository listingTranslationRepository;
    private final DeepLTranslationService deepLTranslationService;

    private final ApplicationEventPublisher eventPublisher;

    public Listing create(UserAuthData authData, String type) {
        User authorProxy = entityManager.getReference(User.class, authData.sub());
        Listing listing = new Listing(authorProxy, type);
        return listingRepository.save(listing);
    }

    @Transactional
    public void delete(UserAuthData authData, Long listingId) {

        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        log.debug("Удаляем объявление");
        listingRepository.delete(listing);

        eventPublisher.publishEvent(new ListingDeletedEvent(listingId));
    }

    public void addListingToFavorite(UserAuthData authData, Long listingId) {
        listingRepository.addFavoriteListing(authData.sub(), listingId);
    }

    public void removeListingFromFavorite(UserAuthData authData, Long listingId) {
        listingRepository.removeFavoriteListing(authData.sub(), listingId);
    }

    public void publish(UserAuthData authData, Long listingId) {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        if (!listing.isTemporary()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Объявление уже опубликовано");
        }

        listing.setTemporary(false);
        listing.setPublishedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }

    public void modifyListingParam(
        UserAuthData authData, 
        Long listingId, 
        ListingDTO.Update updates
    ) throws AccessDeniedException {

        securityFilterService.listingUpdateFilter(authData, listingId);

        log.debug("listing update \n{} \n{} \n{} \n{} \n{} \n{} \n{} \n{}",
            updates.price(),
            updates.priceType(),
            updates.locationId(),
            updates.categoryId(),
            updates.mainImageId(),
            updates.accessToken(),
            updates.active(),
            updates.testMode()
        );

        Listing listing = listingQueryService.getListingById(listingId);

        Image image = imageRepository.findById(updates.mainImageId()).orElse(null);

        listing.setPrice(updates.price());
        
        if (image.getListingId() == listingId) {
            listing.setImagePath(image.getLink());
        }

        if (!listing.getPublicType().equals(ListingPublicType.PRODUCT_GIVEAWAY) && 
            !listing.getPublicType().equals(ListingPublicType.PRODUCT_SWAP) &&
            !listing.getPublicType().equals(ListingPublicType.PRODUCT_WANTED_FREE)) {

            listing.setPriceType(updates.priceType());
        }

        listing.setActive(updates.active());
        
        if (updates.accessToken() != null) {
            listing.setAccessToken(updates.accessToken());
        }

        listing.setTestMode(updates.testMode());

        Long locId = updates.locationId(); // безопасно для Integer и Long
        Location loc = locationRepository.findById(locId).orElse(null);
        listing.setLocation(loc);

        Long catId = updates.categoryId(); // безопасно для Integer и Long
        switch (listing.getType()) {
            case PRODUCT:
                ProductCategory pCat = productCategoryRepository.findById(catId).orElse(null);
                listing.getProductSettings().setCategory(pCat);
                break;
            case SERVICE:
                ServiceCategory sCat = serviceCategoryRepository.findById(catId).orElse(null);
                listing.getServiceSettings().setCategory(sCat);
                break;
            default:
                break;
        }

        listingRepository.save(listing);
    }

    @Transactional
    public Set<String> updateListingTranslations(
        UserAuthData authData,
        Long listingId, 
        Map<String, ListingTranslationDTO> translationsMap,
        ListingTranslateType translateType
    ) {

        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        Map<String, ListingTranslation> currentTranslations = listing.getTranslations();

        log.debug("currentTranslations: {}", new ArrayList<>(currentTranslations.keySet()));

        Set<String> newLanguages = new HashSet<>(currentTranslations.keySet());

        LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(LanguageUtils.SUPPORTED_LANGUAGES_LINGUA).build();

        for (Map.Entry<String, ListingTranslationDTO> entry : translationsMap.entrySet()) {

            String lang = entry.getKey();
            ListingTranslationDTO dto = entry.getValue();

            String title = dto.title();
            String description = dto.description();

            log.debug("incoming {} -> title: {}", lang, title);

            // Определяем язык, если пришёл undetected
            if (lang != null && "undetected".equalsIgnoreCase(lang.trim())) {
                if (title != null && !title.isBlank()) {
                    lang = LanguageMapper.toShortCode(detector.detectLanguageOf(title));
                } else if (description != null && !description.isBlank()) {
                    lang = LanguageMapper.toShortCode(detector.detectLanguageOf(description));
                }
            }

            // Добавляем язык, если есть данные
            if ((title != null && !title.isBlank()) ||
                (description != null && !description.isBlank())) {

                log.debug("using lang {} -> {}", lang, title);
                newLanguages.add(lang);
            }

            // update or create translation
            if (currentTranslations.containsKey(lang)) {
                log.debug("update {}", lang);
                ListingTranslation tr = currentTranslations.get(lang);
                tr.setTitle(title);
                tr.setDescription(description);

                listingTranslationRepository.save(tr);

            } else {
                log.debug("create {}", lang);
                ListingTranslation tr = new ListingTranslation(lang, title, description, listing, translateType);
                
                listingTranslationRepository.save(tr);

                currentTranslations.put(lang, tr);
            }
        }

        // удаляем переводы, которых больше нет
        currentTranslations.keySet().removeIf(lang -> !newLanguages.contains(lang));

        return newLanguages;
    }

    public ListingTranslationDTO autoTranslateListing(
        UserAuthData authData, 
        Long listingId, 
        String lang, 
        String preferedRefLang
    ) {
        securityFilterService.listingAuthorFilter(authData, listingId);

        List<ListingTranslation> translations = listingTranslationRepository.findByListingId(listingId);

        if (translations.size() == 0) throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, 
            "At least one text must be provided for translation");

        ListingTranslation reference = translations.stream()
            .filter(l -> Objects.equals(l.getLanguage(), preferedRefLang)) // find prefered
            .findFirst()
            .orElse( // if prefered not found, find hand mate
                translations.stream()
                    .filter(l -> Objects.equals(l.getType(), ListingTranslateType.HAND_MATE))
                    .findFirst()
                    .orElse( // if hand mate not found -> find any
                        translations.getFirst()));

        ListingTranslationDTO translated = deepLTranslationService.translate(
            new ListingTranslationDTO(
                reference.getTitle(), 
                reference.getDescription()
            ), 
            lang, 
            reference.getLanguage()
        );

        log.debug(translated.toString());

        updateListingTranslations(authData, listingId, Map.of(lang, translated), ListingTranslateType.DEEPL);

        return translated;
    }
}