package org.workswap.sso.core.security.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.sso.core.security.service.AuthService;
import org.workswap.sso.core.user.UserCommandService;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.datasource.model.UserDevice;
import org.workswap.sso.datasource.repository.UserDeviceRepository;
import org.workswap.sso.datasource.repository.UserRepository;
import org.workswap.sso.dto.AuthResponse;
import org.workswap.sso.dto.LoginRequest;
import org.workswap.sso.dto.RegisterRequest;
import org.workswap.sso.dto.UserDeviceDTO;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.AuthProvider;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.sso.security.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserCommandService userCommandService;
    private final AuthCookiesService cookiesService;
    private final UserDeviceRepository userDeviceRepository;
    
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(request.email()).orElse(null);

        if (user == null) {
            return new AuthResponse(false, "Такого аккаунта не существует", null);
        }

        if (!user.getProvider().contains(AuthProvider.LOCAL)) {
            return new AuthResponse(false, "В этот аккаунт нельзя зайти паролем", null);
        }

        if (!userCommandService.authenticate(user, request.password())) {
            return new AuthResponse(false, "Неверный логин или пароль", null);
        }

        try {
            cookiesService.setAuthCookies(response, user);
        } catch (ServletException e) {
            e.printStackTrace();
        }

        if (user.getStatus().equals(UserStatus.PENDING)) {
            return new AuthResponse(true, "Требуется верификация email", Map.of("verifyEmail", true));
        }

        return new AuthResponse(true, "Вы успешно авторизовались", null);
    }

    public AuthResponse registerLocal(
        RegisterRequest regRequest, 
        HttpServletRequest request,
        HttpServletResponse response
    ) {

        if (userRepository.existsByName(regRequest.name())) {
            return new AuthResponse(false, "Такое имя уже зарегистрировано", null);
        }

        if (userRepository.existsByEmail(regRequest.email())) {
            return new AuthResponse(false, "Такой email уже зарегистрирован", null);
        }

        User user = userCommandService.registerLocal(regRequest, request);

        try {
            cookiesService.setAuthCookies(response, user);
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return new AuthResponse(true, "Вы успешно зарегистрировались", Map.of("verifyEmail", true));
    }

    public void refreshToken( HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Обновляем токен пользователя");
        try {

            String userSub = jwtService.validateAndGetUserSub(getTokenFromCookies(request, "refreshToken"));

            logger.debug("userSub: {}", userSub);

            UserDeviceDTO dto = userCommandService.createUserDeviceDto(request);

            User user = null;
            if (userSub != null) {
                user = userRepository.findBySub(userSub).orElse(null);
                logger.debug("Пользователь найден по id: {}", user != null);
                if (user != null) {
                    linkOrCreateDeviceForUser(user, dto);
                }
            }

            if (user == null) {
                user = findTempUserByDevice(dto);
                if (user != null) logger.debug("Нашли пользователя по устройству");
            }

            if (user == null) {
                logger.debug("Пользователь не найден, создаём временного");
                user = userCommandService.createTempUser(request);
            }

            logger.debug("Айди пользователя: {}", user.getId());

            userRepository.touchLastUsed(user.getId(), LocalDateTime.now());

            cookiesService.setAuthCookies(response, user); // 4. обновляем куки с токенами

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка обновления токена");
        }
    }

    public AuthResponse registerOauth(UserAuthData authData) {
        
        User updatedUser = userCommandService.registerOauthUser(authData);

        if (updatedUser.getStatus() == UserStatus.ACTIVE) {
            return new AuthResponse(true, "Вы успешно авторизовались", null);
        }
        
        return new AuthResponse(true, "Ошибка регистрации", null);
    }

    public void redirectToGoogle(
        String redirect,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        logger.debug("redirect: " + redirect);

        String userSub = jwtService.validateAndGetUserSub(getTokenFromCookies(request, "refreshToken"));

        request.getSession().setAttribute("tempUserSub", userSub);

        if (redirect != null) {
            request.getSession().setAttribute("redirectUrl", redirect);
        }

        response.sendRedirect("/oauth2/authorization/google");
    }

    private void linkOrCreateDeviceForUser(User user, UserDeviceDTO dto) {

        boolean exists = userDeviceRepository.findDevice(
            user,
            dto.fingerprint(),
            dto.userAgent(),
            dto.ip()
        ).isPresent();

        if (!exists) {
            userDeviceRepository.save(
                new UserDevice(user, dto.fingerprint(), dto.userAgent(), dto.ip())
            );
        }
    }

    private User findTempUserByDevice(UserDeviceDTO dto) {
        return userDeviceRepository.findTempDevice(
            dto.fingerprint(),
            dto.userAgent(),
            dto.ip()
        ).map(UserDevice::getUser)
        .orElse(null);
    }

    private String getTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
