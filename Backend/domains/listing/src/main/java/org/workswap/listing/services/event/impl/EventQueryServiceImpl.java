package org.workswap.listing.services.event.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.dto.EventDTO;
import org.workswap.listing.dto.FullListingDTO;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.listing.services.event.EventQueryService;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class EventQueryServiceImpl implements EventQueryService {

    private final ListingRepository listingRepository;

    private final ListingTranslationRepository translationRepository;
    private final SecurityFilterService securityFilterService;
    private final ListingQueryService listingQueryService;
    private final ApplicationEventPublisher eventPublisher;
    
    public boolean existEventParticipant(UserAuthData authData, Long eventId) {
        return listingRepository.existsParticipant(eventId, authData.sub());
    }

    public EventDTO.Settings getEventSettingsDTO(UserAuthData authData, Long eventId) {

        securityFilterService.listingUpdateFilter(authData, eventId);

        Listing event = listingQueryService.getListingById(eventId);

        return EventDTO.Settings.ofListing(event);
    }

    public List<ShortUserDTO> getEventParticipants(UserAuthData authData, Long eventId) {

        Listing event = listingQueryService.getListingById(eventId);

        if (securityFilterService.listingAuthorFilter(authData, eventId)) {
            List<ShortUserDTO> list = new ArrayList<>();
            for (User participant : event.getEventSettings().getParticipants()) {
                list.add(ShortUserDTO.ofUser(participant));
            }

            return list;
        } else {
            return new ArrayList<>();
        }
    }

    public EventDTO.Page getEventPage(Optional<UserAuthData> optAuthData, String token, Long eventId, String locale) {
        if (eventId == null) {
            throw new IllegalStateException("ID событмя отсутствует");
        }
        Listing listing = listingRepository.findById(eventId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Событие не найдено отсутствует"));

        boolean isAuthor = false;

        ListingTranslation translation = translationRepository.findBestTranslation(eventId, locale);

        EventSettings event = listing.getEventSettings();

        ShortUserProfileDTO author = ShortUserProfileDTO.ofUser(listing.getAuthor());
        List<ImageDTO> images = listing.getImages().stream()
            .map(image -> new ImageDTO(image.getId(), eventId, image.getLink())).toList();

        List<ShortUserDTO> participants = ShortUserDTO.ofList(event.getParticipants());

        if (optAuthData.isPresent()) {
            UserAuthData authData = optAuthData.get();

            isAuthor = securityFilterService.listingAuthorFilter(authData, eventId);

            eventPublisher.publishEvent(
                new ListingViewedEvent(
                    authData.sub(), 
                    eventId, 
                    authData.status().equals(UserStatus.TEMP), 
                    LocalDateTime.now()
                ));
        }

        FullListingDTO listingDto = FullListingDTO.ofListingForListingPage(listing, translation);

        EventDTO.Settings settings = EventDTO.Settings.ofListing(listing);

        EventDTO.Page dto = new EventDTO.Page(
            listingDto,
            author,
            images,

            settings,
            isAuthor ? participants : null,
            participants.size()
        );
        return dto;
    }
}
