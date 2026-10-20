package org.workswap.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.permission.dto.RoleDTO;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.model.UserSettings;

public record FullUserDTO(
    Long id,
    String sub,
    String name,
    String phone,
    String email,
    String bio,
    String avatarUrl,

    List<String> languages,
    List<RoleDTO> roles,

    Long locationId,
    String avatarType,
    Double rating,
    boolean telegramConnected,
    LocalDateTime createdAt,

    String googleAvatar,
    String uploadedAvatar,
    boolean phoneVisible,
    boolean emailVisible
) {
    public static FullUserDTO ofUser(User user) {

        if (user == null) return null;
            
        UserSettings settings = user.getSettings();

        FullUserDTO dto = new FullUserDTO(
            user.getId(),
            user.getSub(),
            user.getName(),
            user.getPhone(),
            user.getEmail(),
            user.getBio(),
            user.getAvatarUrl(),
            user.getLanguages(),
            RoleDTO.ofList(user.getRoles()),
            user.getLocation() != null ? user.getLocation().getId() : null,
            settings.getAvatarType(),
            user.getRating(),
            settings.isTelegramConnected(),
            user.getCreatedAt(),
            settings.getGoogleAvatar(),
            settings.getUploadedAvatar(),
            settings.isPhoneVisible(),
            settings.isEmailVisible()
        );

        return dto;
    }
}