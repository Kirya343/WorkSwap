package org.workswap.listing.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.listing.dto.EventDTO;
import org.workswap.listing.services.event.EventCommandService;
import org.workswap.listing.services.event.EventQueryService;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.annotations.parameters.OptionalAuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.dto.ShortUserDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/event")
public class EventController {

    private final EventQueryService eventQueryService;
    private final EventCommandService eventCommandService;
    
    @GetMapping("/{eventId}")
    @PublicEndpoint
    public EventDTO.Page getEventListing(
        @OptionalAuthUser Optional<UserAuthData> authData,
        @PathVariable Long eventId, 
        @RequestParam(required = false) String token,
        @RequestParam String locale
    ) {
        return eventQueryService.getEventPage(authData, token, eventId, locale);
    }
    
    @GetMapping("/{eventId}/participants")
    @RequiredPermission("GET_EVENT_PARTICIPANTS")
    public List<ShortUserDTO> getEventPaticipants(
        @AuthUser UserAuthData authData, 
        @PathVariable Long eventId
    ) {
        return eventQueryService.getEventParticipants(authData, eventId);
    }

    @GetMapping("/{eventId}/participants/check")
    @RequiredPermission("CHECK_EVENT_PARTICIPANTS")
    public boolean checkEventPaticipant(
        @AuthUser UserAuthData authData, 
        @PathVariable Long eventId
    ) {
        return eventQueryService.existEventParticipant(authData, eventId);
    }

    @PostMapping("/{eventId}/participants")
    @RequiredPermission("ADD_EVENT_PARTICIPANT")
    public void addEventPaticipant(
        @AuthUser UserAuthData authData, 
        @PathVariable Long eventId
    ) {
        eventCommandService.addEventParticipant(authData, eventId);
    }

    @DeleteMapping("/{eventId}/participants")
    @RequiredPermission("REMOVE_EVENT_PARTICIPANT")
    public void removeEventPaticipant(
        @AuthUser UserAuthData authData, 
        @PathVariable Long eventId 
    ) {
        eventCommandService.removeEventParticipant(authData, eventId);
    }

    @PatchMapping("/{eventId}/modify")
    @RequiredPermission("UPDATE_LISTING")
    public void modifyListing(
        @AuthUser UserAuthData authData,
        @PathVariable Long eventId,
        @RequestBody Map<String, Object> updates
    ) throws AccessDeniedException {
        eventCommandService.modifyEventParam(authData, eventId, updates);
    }

    @GetMapping("/{eventId}/settings")
    @RequiredPermission("GET_EVENT_SETTINGS")
    public EventDTO.Settings getEventSettings(
        @AuthUser UserAuthData authData,
        @PathVariable Long eventId
    ) {
        return eventQueryService.getEventSettingsDTO(authData, eventId);
    }
}
