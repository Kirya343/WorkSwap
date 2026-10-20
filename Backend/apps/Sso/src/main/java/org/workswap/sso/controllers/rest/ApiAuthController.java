package org.workswap.sso.controllers.rest;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.sso.core.security.service.AuthCookiesService;
import org.workswap.sso.core.security.service.AuthService;
import org.workswap.sso.core.user.UserCommandService;
import org.workswap.sso.dto.AuthResponse;
import org.workswap.sso.dto.LoginRequest;
import org.workswap.sso.dto.RegisterRequest;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final AuthCookiesService cookiesService;
    private final UserCommandService userCommandService;
    private final AuthService authService;

    @GetMapping("/google")
    @PublicEndpoint
    public void redirectToGoogle(
        @RequestParam(required = false) String redirect,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        authService.redirectToGoogle(redirect, request, response);
    }

    @PostMapping("/login")
    @PublicEndpoint
    public AuthResponse login(
        @RequestParam(required = false) String redirect,
        @Valid @RequestBody LoginRequest request, 
        HttpServletResponse response
    ) throws IOException, ServletException {
        return authService.login(request, response);
    }

    @PostMapping("/register")
    @PublicEndpoint
    public AuthResponse register(
        @Valid @RequestBody RegisterRequest regRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        return authService.registerLocal(regRequest, request, response);
    }

    @PostMapping("/verify")
    @Authenticated
    public void verifyEmail(
        @AuthUser UserAuthData authData, 
        @RequestParam String code
    ) {
        if (!userCommandService.verifyEmail(authData, code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send-verify-code")
    @Authenticated
    public void sendVerifyCode(@AuthUser UserAuthData authData) {
        userCommandService.sendVerificationCode(authData);
    }

    @PostMapping("/refresh")
    @PublicEndpoint
    public void refreshToken(
        HttpServletRequest request, 
        HttpServletResponse response
    ) {
        authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    @PublicEndpoint
    public void logout(HttpServletResponse response) throws ServletException {
        cookiesService.deleteAuthCookies(response);
    }

    @PatchMapping("/google/register")
    @Authenticated
    public AuthResponse registerOauth(@AuthUser UserAuthData authData) {
        return authService.registerOauth(authData);
    }
}