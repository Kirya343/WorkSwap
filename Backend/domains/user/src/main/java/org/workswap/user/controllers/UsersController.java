package org.workswap.user.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.user.services.UserCommandService;
import org.workswap.user.services.UserQueryService;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserControlPageRequest;
import org.workswap.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/user")
public class UsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    @PostMapping("/telegram")
    @RequiredPermission("CONNECT_TELEGRAM")
    public String telegramConnect(@AuthUser UserAuthData authData) {
        return userCommandService.connectTelegram(authData);
    }

    @GetMapping("/telegram")
    @RequiredPermission("CONNECT_TELEGRAM")
    public Boolean checkTelegramConnect(@AuthUser UserAuthData authData) {
        return userQueryService.checkTelegramConnect(authData);
    }
    
    @DeleteMapping
    @RequiredPermission("DELETE_OWN_ACCOUNT")
    public void deleteAccount(@AuthUser UserAuthData authData) {
        userCommandService.deleteUser(authData);
    }

    @GetMapping
    @RequiredPermission("GET_CURRENT_USER")
    public UserDTO getCurrentUser(@AuthUser UserAuthData authData) {
        return userQueryService.getCurrentUser(authData);
    }

    @GetMapping("/settings")
    @RequiredPermission("GET_CURRENT_USER_SETTINGS")
    public FullUserDTO getCurrentUserSettings(@AuthUser UserAuthData authData) {
        return userQueryService.getFullUserDTO(authData);
    }

    @GetMapping("/{userSub}")
    @PublicEndpoint
    public ShortUserDTO getUser(@PathVariable String userSub) {
        return userQueryService.getBySub(userSub);
    }

    @GetMapping("/recent")
    @RequiredPermission("GET_RECENT_USERS")
    public List<UserDTO> getRecentUsers(@RequestParam int amount) {
        return userQueryService.getRecentUsers(amount);
    }

    @PatchMapping("/modify")
    @RequiredPermission("UPDATE_USER_SETTINGS")
    public void modifyUser(
        @AuthUser UserAuthData authData,
        @RequestBody Map<String, Object> updates
    ) {
        userCommandService.modifyUserParam(authData, updates);
    }

    @GetMapping("/{userSub}/profile")
    @PublicEndpoint
    public ShortUserProfileDTO getUserProfile(@PathVariable String userSub) {
        return userQueryService.getUserProfile(userSub);
    }

    @GetMapping("/{userSub}/full-info")
    @RequiredPermission("GET_FULL_USER_INFO")
    public UserControlPageRequest getUserControlPage(
        @PathVariable String userSub
    ) {
        return userQueryService.getUserControlPage(userSub);
    }

    @GetMapping("/list")
    @RequiredPermission("GET_USERS_LIST")
    public Page<UserDTO> getUsersList(
        @RequestParam int size, 
        @RequestParam int page, 
        @RequestParam String sortParam
    ) {
        return userQueryService.getUsersList(size, page, sortParam);
    }

    @PostMapping("/role")
    @RequiredPermission("ADD_USER_ROLE")
    public void userAddRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userRepository.addRoleToUser(userId, roleId);
    }

    @DeleteMapping("/role")
    @RequiredPermission("REMOVE_USER_ROLE")
    public void userRemoveRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userRepository.removeRoleFromUser(userId, roleId);
    }
}
