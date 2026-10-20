package org.workswap.listing.services.event;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.repository.types.EventSettingsRepository;
import org.workswap.listing.enums.EventStatus;
import org.workswap.listing.enums.RecurrencePattern;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.sso.security.dto.UserAuthData;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class EventCommandService {

    private static final Logger logger = LoggerFactory.getLogger(EventCommandService.class);

    private final SecurityFilterService securityFilterService;
    private final EventSettingsRepository eventRepository;
    
    public void addEventParticipant(UserAuthData authData, Long eventId) {
        eventRepository.addParticipant(eventId, authData.sub());
    }

    public void removeEventParticipant(UserAuthData authData, Long eventId) {
        eventRepository.removeParticipant(eventId, authData.sub());
    }

    public void modifyEventParam(UserAuthData authData, Long eventId, Map<String, Object> updates) throws AccessDeniedException {

        logger.debug("Айди пользователя: {}", authData.sub());

        if (eventId == null) {
            throw new IllegalStateException("ID события отсутствует");
        }

        securityFilterService.listingUpdateFilter(authData, eventId);
        
        EventSettings event = eventRepository.findById(eventId).orElseThrow(
            () -> new EntityNotFoundException("Ивент с таким Id не найден"));

        if (event != null) {
            updates.forEach((key, value) -> {
                logger.debug("Обновляем часть объявления: {}", key );
                if (value != null) {
                    switch (key) {

                        case "eventDate":
                            if (event != null) {
                                LocalDateTime date = LocalDateTime.parse((String) value);
                                event.setEventDate(date);
                            }
                            break;
                        case "registrationCloseTime":
                            if (event != null) {
                                LocalDateTime date = LocalDateTime.parse((String) value);
                                event.setRegistrationCloseTime(date);
                            }
                            break;
                        case "recurrence":
                            if (event != null) {
                                String rPattern = ((String) value); // безопасно для Integer и Long
                                event.setRecurrencePattern(RecurrencePattern.valueOf(rPattern));
                            }
                            break;
                        case "recurring":
                            if (event != null) {
                                event.setRecurring((Boolean) value);
                            }
                            break;
                        case "isPublic":
                            if (event != null) {
                                event.setPublic((Boolean) value);
                            }
                            break;
                        case "maxParticipants":
                            if (event != null) {
                                Integer number;
                                if (value instanceof Number) {
                                    number = ((Number) value).intValue();
                                } else {
                                    number = Integer.parseInt(value.toString());
                                }
                                event.setMaxParticipants(number);
                            }
                            break;
                        case "minParticipants":
                            if (event != null) {
                                Integer number;
                                if (value instanceof Number) {
                                    number = ((Number) value).intValue();
                                } else {
                                    number = Integer.parseInt(value.toString());
                                }
                                event.setMinParticipants(number);
                            }
                            break;
                        case "eventStatus":
                            if (event != null) {
                                EventStatus status = EventStatus.valueOf((String) value);
                                event.setEventStatus(status);
                            }
                            break;
                    }
                }
            });
            
            eventRepository.save(event);
        }
    }
}
