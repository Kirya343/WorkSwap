package org.workswap.user.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.user.datasource.model.User;

public record ShortUserDTO(
    String sub,
    String name,
    String avatarUrl
) {
    public static ShortUserDTO ofUser(User user) {
        
        if (user == null) return null;

        return new ShortUserDTO(
            user.getSub(), 
            user.getName(), 
            user.getAvatarUrl()
        );
    }

    public static List<ShortUserDTO> ofList(Collection<User> users) {
        return users.stream().map(user -> ShortUserDTO.ofUser(user)).toList();
    }
}
