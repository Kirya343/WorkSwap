package org.workswap.sso.core.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.sso.amqp.UserProducer;
import org.workswap.sso.core.notification.EmailService;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.datasource.model.VerifyCode;
import org.workswap.sso.datasource.repository.UserRepository;
import org.workswap.sso.datasource.repository.VerifyCodeRepository;
import org.workswap.sso.dto.RegisterRequest;
import org.workswap.sso.dto.UserDeviceDTO;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.AuthProvider;
import org.workswap.sso.security.enums.UserStatus;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private static final Logger logger = LoggerFactory.getLogger(UserCommandService.class);

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final UserProducer userProducer;
    private final UserRepository userRepository;
    private final VerifyCodeRepository verifyCodeRepository;

    public UserDeviceDTO createUserDeviceDto(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        String fingerprint = request.getHeader("X-Fingerprint");

        logger.debug("ip: {}", ip);
        logger.debug("userAgent: {}", userAgent);
        logger.debug("fingerprint: {}", fingerprint);
        
        return new UserDeviceDTO(fingerprint, userAgent, ip);
    }

    public User registerOrUpdateUser(OidcUser oidcUser, Optional<String> tempUserSub, HttpServletRequest request) {
        String email = oidcUser.getEmail();
        User existingUser = userRepository.findByEmail(email).orElse(null);
        boolean isNew = existingUser == null || existingUser.getStatus() == UserStatus.PENDING;

        User user = isNew
            ? (existingUser != null ? existingUser : createNewUser(oidcUser, request))
            : existingUser;

        if (user == null) {
            throw new IllegalStateException("User creation failed: user is null for " + email);
        }

        if (isNew) {
            String sub = tempUserSub.get();

            if (sub != null) {
                User tempUser = userRepository.findBySub(sub).orElseThrow();

                if (tempUser.getStatus() == UserStatus.TEMP) {
                    userRepository.delete(tempUser);
                }
            }
        }

        if (user.getAvatarUrl() == null) {
            user.setAvatarUrl(oidcUser.getPicture());
        }
        return userRepository.save(user);
    }

    public User createNewUser(OidcUser oidcUser, HttpServletRequest request) {
        UserDeviceDTO dto = createUserDeviceDto(request);

        User user = new User(
            oidcUser.getClaim("name"),
            oidcUser.getEmail(),
            oidcUser.getPicture(),
            AuthProvider.GOOGLE,
            false, 
            dto.fingerprint(), 
            dto.userAgent(), 
            dto.ip()
        );

        User newUser = userRepository.save(user);

        newUser.setNew(true);
        
        return newUser;
    }

    public User registerOauthUser(UserAuthData authData) {

        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        user.setStatus(UserStatus.ACTIVE);
        user.setTermsAccepted(true);
        user.setTermsAcceptanceDate(LocalDateTime.now());

        User saved = userRepository.save(user);

        userProducer.userCreated(saved);
        
        return saved;
    }

    public User createTempUser(HttpServletRequest request) {

        UserDeviceDTO dto = createUserDeviceDto(request);

        User user = new User(
            UserStatus.TEMP,
            dto.fingerprint(), 
            dto.userAgent(), 
            dto.ip()
        );

        User saved = userRepository.save(user);

        userProducer.userCreated(saved);
        
        return saved;
    }

    public User registerLocal(RegisterRequest regRequest, HttpServletRequest request) {
        UserDeviceDTO dto = createUserDeviceDto(request);
        
        String hashed = passwordEncoder.encode(regRequest.password());

        String email = regRequest.email();
        User user = new User(
            regRequest.name(), 
            email, 
            hashed,
            true, 
            dto.fingerprint(), 
            dto.userAgent(), 
            dto.ip()
        );

        User saved = userRepository.save(user);

        userProducer.userCreated(saved);
        
        return saved;
    }

    public void sendVerificationCode(UserAuthData authData) {

        if (!authData.status().equals(UserStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ваш аккаунт не нуждается в верификации");
        }

        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        String email = user.getEmail();

        VerifyCode verifyCode = verifyCodeRepository.findByEmail(email);

        if (verifyCode == null) {
            verifyCode = verifyCodeRepository.save(new VerifyCode(email));
        } 

        if (verifyCode.getSentAt() != null && verifyCode.getSentAt().isAfter(LocalDateTime.now().minus(Duration.ofSeconds(45)))) {
            throw new ResponseStatusException(HttpStatus.TOO_EARLY, "Нельзя отправлять запросы на письма так часто");
        }

        emailService.sendVerificationEmail(email, verifyCode.getCode());

        verifyCode.setSentAt(LocalDateTime.now());
        verifyCodeRepository.save(verifyCode);
    }

    @Transactional
    public boolean verifyEmail(UserAuthData authData, String code) {

        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        String email = user.getEmail();

        if (verifyCodeRepository.existsByEmailAndCode(email, code)) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            verifyCodeRepository.deleteByEmail(email);
            return true;
        } else {
            return false;
        }
    }

    public boolean authenticate(User user, String rawPassword) {
        if (user == null) return false;
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    @Transactional
    public void deleteUser(UserAuthData authData) {
        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        try {
            if (user == null) {
                throw new RuntimeException("Пользователя не зарегистрировано.");
            }

            logger.debug("Пользователь {} найден, начинаем удаление", user.getId());

            // Удаление пользователя
            try {
                Long userId = user.getId();

                if (userId == null) {
                    throw new RuntimeException("Id пользователя не найдено!");
                }
                logger.debug("Удаление пользователя {}", userId);
                userRepository.deleteById(userId);
            } catch (Exception e) {
                logger.error("Ошибка при удалении пользователя {}: {}", user.getId(), e.getMessage(), e);
                throw new RuntimeException("Ошибка при удалении пользователя", e);
            }

        } catch (Exception e) {
            logger.error("Не удалось удалить пользователя через OAuth2: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении пользователя через OAuth2", e);
        }
    }

    public void acceptTerms(UserAuthData authData) {
        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        user.setTermsAcceptanceDate(LocalDateTime.now());
        user.setTermsAccepted(true);
        
        userRepository.save(user);
    }
}
