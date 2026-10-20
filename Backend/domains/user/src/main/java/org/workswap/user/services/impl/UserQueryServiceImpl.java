package org.workswap.user.services.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.datasource.repository.UserSettingsRepository;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.UserControlPageRequest;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserDTO;
import org.workswap.user.services.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    public User findUserFromOAuth2(OAuth2User oauth2User) {
        User user = userRepository.findByEmail(oauth2User.getAttribute("email")).orElseThrow();
        return user;
    }

    public List<UserDTO> getRecentUsers(int count) {
        List<User> users = userRepository.findAllByStatusOrderByCreatedAtDesc(PageRequest.of(0, count), UserStatus.ACTIVE).getContent();

        return UserDTO.ofList(users);
    }

    public boolean checkTelegramConnect(UserAuthData authData) {
        return userSettingsRepository.existsByUserSubAndTelegramConnectedTrue(authData.sub());
    }

    public List<User> findAllStandartUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    public UserDTO getCurrentUser(UserAuthData authData) {
        User user = userRepository.getFullUser(authData.sub()).orElseThrow();
        return UserDTO.ofUser(user);
    }

    public ShortUserDTO getById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        User user = userRepository.findById(userId).orElseThrow();
        return ShortUserDTO.ofUser(user);
    }

    public ShortUserDTO getBySub(String userSub) {
        User user = userRepository.findBySub(userSub).orElseThrow();
        return ShortUserDTO.ofUser(user);
    }

    public ShortUserProfileDTO getUserProfile(String userSub) {
        User user = userRepository.findBySub(userSub).orElseThrow();

        return ShortUserProfileDTO.ofUser(user);
    }

    public UserControlPageRequest getUserControlPage(String userSub) {
        User user = userRepository.findBySub(userSub).orElseThrow(
            () -> new IllegalStateException("User not found"));

        FullUserDTO userDto = FullUserDTO.ofUser(user);

        return new UserControlPageRequest(userDto);
    }

    public FullUserDTO getFullUserDTO(UserAuthData authData) {
        User user = userRepository.getFullUser(authData.sub()).orElseThrow();
        return FullUserDTO.ofUser(user);
    }

    public Page<UserDTO> getUsersList(int size, int page, String sortParam) {

        if (sortParam == null || sortParam.length() == 0) sortParam = "createdAt";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam).descending());
        Page<Long> ids = userRepository.findIds(pageable);
        List<User> users = userRepository.findWithRelationsByIds(ids.getContent());

        return new PageImpl<>(
            UserDTO.ofList(users), 
            pageable, 
            ids.getTotalElements());
    }
}
