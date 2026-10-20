package org.workswap.statistic.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.statistic.amqp.producers.SiteViewProducer;
import org.workswap.statistic.dto.SiteViewDTO;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@Profile("server")
@RequiredArgsConstructor
public class QrSimpleController {

    // инжектируйте репозиторий/сервис для работы со счётчиками
    private final SiteViewProducer siteViewProducer;

    @GetMapping("/r/{codeName}")
    @PublicEndpoint
    public ResponseEntity<?> redirect(
            @PathVariable String codeName,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // 1) Попытаться прочитать cookie visit_id
        String visitId = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("visit_id".equals(c.getName())) {
                    visitId = c.getValue();
                    break;
                }
            }
        }

        boolean isNew = false;
        if (visitId == null || visitId.isBlank()) {
            // 2) Если нет — создать и поставить cookie
            visitId = UUID.randomUUID().toString();
            ResponseCookie cookie = ResponseCookie.from("visit_id", visitId)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(365 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

            response.addHeader("Set-Cookie", cookie.toString());
            isNew = true;
        }

        if (isNew) {
            SiteViewDTO dto = new SiteViewDTO(
                codeName,
                LocalDateTime.now()
            );
            siteViewProducer.sendSiteView(dto);
        }

        // 5) Редирект на нужную страницу
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create("https://workswap.org/"));
        
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
