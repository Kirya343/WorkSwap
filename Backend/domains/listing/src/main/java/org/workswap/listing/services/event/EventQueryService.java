package org.workswap.listing.services.event;

import java.util.List;
import java.util.Optional;

import org.workswap.listing.dto.EventDTO;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.dto.ShortUserDTO;

public interface EventQueryService {
    boolean existEventParticipant(UserAuthData authData, Long eventId);

    List<ShortUserDTO> getEventParticipants(UserAuthData authData, Long eventId);
    EventDTO.Settings getEventSettingsDTO(UserAuthData authData, Long eventId);
    EventDTO.Page getEventPage(Optional<UserAuthData> optAuthData, String token, Long eventId, String locale);
}
