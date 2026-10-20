package org.workswap.user.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.workswap.user.services.UserCommandService;
import org.workswap.permission.datasource.model.Role;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.rabbit.queues.events.UserCreatedEvent;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.model.UserSettings;
import org.workswap.permission.datasource.repository.RoleRepository;
import org.workswap.user.datasource.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Profile("server")
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger logger = LoggerFactory.getLogger(UserCommandService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public void deleteUser(UserAuthData authData) {
        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        if (user == null) {
            throw new RuntimeException("Пользователя не зарегистрировано.");
        }

        logger.debug("Пользователь {} найден, начинаем удаление", user.getId());
        
        user.setStatus(UserStatus.DEACTIVATED);

        userRepository.save(user);
    }

    public void modifyUserParam(UserAuthData authData, Map<String, Object> updates) {

        User user = userRepository.findBySubWithSettings(authData.sub());
        UserSettings settings = user.getSettings();

        if (user != null) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        user.setName((String) value);
                        break;
                    case "phone":
                        user.setPhone((String) value);
                        break;
                    case "phoneVisible":
                        settings.setPhoneVisible((Boolean) value);
                        break;
                    case "emailVisible":
                        settings.setEmailVisible((Boolean) value);
                        break;
                    case "avatarType":
                        String avatarType = (String) value;
                        settings.setAvatarType(avatarType);
                        switch (avatarType) {
                            case "google":
                                user.setAvatarUrl(settings.getGoogleAvatar());
                                break;
                            case "uploaded":
                                user.setAvatarUrl(settings.getUploadedAvatar());
                                break;
                            case "default":
                                user.setAvatarUrl("/images/avatar-placeholder.png");
                                break;
                        
                            default:
                                break;
                        }
                        break;
                    case "languages":
                        if (value instanceof List<?> listValue) {
                            user.setLanguages(listValue.stream()
                                .filter(item -> item instanceof String)
                                .map(item -> (String) item)
                                .toList()
                            );
                        } else {
                            logger.warn("Ожидался список, но получили: {}", value.getClass().getName());
                        }
                        break;
                    case "locationId":
                        if (value != null) {
                            Long locId = ((Number) value).longValue(); // безопасно для Integer и Long
                            Location loc = locationRepository.findById(locId).orElse(null);
                            user.setLocation(loc);
                        }
                        break;
                    case "bio":
                        user.setBio((String) value);
                        break;
                }
            });
        }

        userRepository.save(user);
    }

    public String connectTelegram(UserAuthData authData) {
        User user = userRepository.findBySubWithSettings(authData.sub());
        String email = user.getEmail();

        String body = "{\"websiteUserId\":\"" + email + "\"}";
        String signature = /* WebhookSigner.generateSignature(body); */ "";

        try {
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://89.35.130.223:30003/api/users/generate-token"))
                .header("Content-Type", "application/json")
                .header("X-Webhook-Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonMapper objectMapper = JsonMapper.builder().build();
            JsonNode json = objectMapper.readTree(response.body());

            String linkUrl = json.path("data").path("linkUrl").stringValue();

            user.getSettings().setTelegramConnected(true);
            userRepository.save(user);

            return linkUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при отправке запроса");
        }
    }

    public void createUser(UserCreatedEvent event) {

        Role role = roleRepository.findByName("TEMP_USER");
        User user = new User(
            event.sub(),
            event.name(), 
            event.email(), 
            event.avatarUrl(), 
            Set.of(role), 
            UserStatus.valueOf(event.status()));
            
        userRepository.save(user);
    }
}
