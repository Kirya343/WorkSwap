package org.workswap.user.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.permission.dto.RoleDTO;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.model.UserSettings;

public record UserDTO(
    String sub,

    String name,
    String phone,
    String email,

    String bio,
    String avatarUrl,

    List<String> languages,
    List<RoleDTO> roles,

    Double rating,
    LocalDateTime createdAt
) {
    public static UserDTO ofUser(User user) {

        if (user == null) return null;

        UserSettings settings = user.getSettings();
        boolean phoneVisible = false;
        boolean emailVisible = false;
        if (settings != null) {
            phoneVisible = settings.isPhoneVisible();
            emailVisible = settings.isEmailVisible();
        }
                              
        UserDTO dto = new UserDTO(
            user.getSub(), 
            user.getName(), 
            phoneVisible ? user.getPhone() : null, 
            emailVisible ? user.getEmail() : null, 
            user.getBio(), 
            user.getAvatarUrl(),
            user.getLanguages(),
            RoleDTO.ofList(user.getRoles()),
            user.getRating(),
            user.getCreatedAt()
        );
        return dto;
    }

    public static List<UserDTO> ofList(Collection<User> users) {
        return users.stream().map(user -> UserDTO.ofUser(user)).toList();
    }
}
