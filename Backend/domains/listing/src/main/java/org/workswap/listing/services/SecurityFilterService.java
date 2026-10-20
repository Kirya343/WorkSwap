package org.workswap.listing.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.exceptions.ListingAccessDeniedException;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class SecurityFilterService {

    private final ListingRepository listingRepository;
    
    // Фильтр который проверяет, есть ли доступ у человека к этому объявлению (на обновление, более жёсткий)
    public void listingUpdateFilter(UserAuthData authData, Long listingId) {
        // Вся логика проверок в одном месте

        if (!listingAuthorFilter(authData, listingId)) {
            throw new ListingAccessDeniedException();
        }
    }

    // Фильтр который проверяет, есть ли доступ у человека доступ к полной информации объявления
    public boolean listingGetFilter(UserAuthData authData, Listing listing, String token) {

        boolean isAuthor = listingAuthorFilter(authData, listing.getId());

        boolean isOpen = listing.isActive() && !listing.isTemporary() && !listing.isTestMode();

        boolean hasAccessToken = false;
        String accessToken = listing.getAccessToken();

        if (accessToken != null && token != null) {
            hasAccessToken = accessToken.equals(token);
        }

        if (isOpen || isAuthor || hasAccessToken) {
            return true;
        }

        return false;
    }

    public boolean listingAuthorFilter(UserAuthData authData, Long listingId) {
        if (authData == null) return false;
        
        return listingRepository.existsByIdAndAuthorSub(listingId, authData.sub());
    }
}
