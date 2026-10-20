package org.workswap.user.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserDTO;
import org.workswap.user.dto.UserControlPageRequest;

public interface UserQueryService {
    
    User findUserFromOAuth2(OAuth2User oauth2User);
    boolean checkTelegramConnect(UserAuthData authData);

    List<User> findAllStandartUsers();
    
    List<UserDTO> getRecentUsers(int count);
    FullUserDTO getFullUserDTO(UserAuthData authData);

    UserDTO getCurrentUser(UserAuthData authData);
    ShortUserDTO getById(Long userId);
    ShortUserDTO getBySub(String userSub);
    ShortUserProfileDTO getUserProfile(String userSub);
    UserControlPageRequest getUserControlPage(String userSub);

    Page<UserDTO> getUsersList(int size, int page, String sortParam);
}
