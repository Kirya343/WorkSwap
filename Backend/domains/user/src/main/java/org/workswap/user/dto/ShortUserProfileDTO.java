package org.workswap.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.model.UserSettings;

public record ShortUserProfileDTO(
    Long id,
    String sub,

    String name,
    String phone,
    String email,
    String avatarUrl,
    String bio,

    List<String> languages,
    Double rating,
    LocalDateTime createdAt
) {
    public static ShortUserProfileDTO ofUser(User user) {

        UserSettings settings = user.getSettings();
        boolean phoneVisible = false;
        boolean emailVisible = false;
        if (settings != null) {
            phoneVisible = settings.isPhoneVisible();
            emailVisible = settings.isEmailVisible();
        }
        
        return new ShortUserProfileDTO(
            user.getId(),
            user.getSub(), 
            user.getName(), 
            phoneVisible ? user.getPhone() : null, 
            emailVisible ? user.getEmail() : null, 
            user.getAvatarUrl(), 
            user.getBio(),
            user.getLanguages(), 
            user.getRating(), 
            user.getCreatedAt()
        );
    }
}
