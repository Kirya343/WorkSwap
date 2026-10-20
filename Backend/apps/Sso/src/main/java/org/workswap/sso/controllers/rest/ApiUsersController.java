package org.workswap.sso.controllers.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.core.user.UserCommandService;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class ApiUsersController {

    private final UserCommandService userCommandService;
    
    @PostMapping("/accept-terms")
    @Authenticated
    public void acceptTerms(@AuthenticationPrincipal UserAuthData authData) {
        userCommandService.acceptTerms(authData);
    }

    @DeleteMapping("/current/delete")
    @Authenticated
    public void deleteAccount(@AuthenticationPrincipal UserAuthData authData) {
        userCommandService.deleteUser(authData);
    }
}
