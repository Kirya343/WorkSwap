package org.workswap.listing.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.enums.EventStatus;
import org.workswap.listing.enums.RecurrencePattern;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;

public class EventDTO {

    public record Settings(
        LocalDateTime eventDate,
        LocalDateTime registrationCloseTime,
        boolean recurring,
        RecurrencePattern recurrencePattern,
        EventStatus eventStatus,
        boolean isPublic,
        Integer maxParticipants,
        Integer minParticipants
    ) {
        public static Settings ofListing(Listing listing) {

            if (listing == null) {
                return null;
            }

            EventSettings settings = listing.getEventSettings();

            return new EventDTO.Settings(
                settings.getEventDate(),
                settings.getRegistrationCloseTime(),
                settings.isRecurring(),
                settings.getRecurrencePattern(),
                settings.getEventStatus(),
                settings.isPublic(),
                settings.getMaxParticipants(),
                settings.getMinParticipants()
            );
        }
    }

    public record Page (
        FullListingDTO listing,
        ShortUserProfileDTO author,
        List<ImageDTO> images,

        Settings event,
        List<ShortUserDTO> participants,
        int participantsCount
    ) {}
}