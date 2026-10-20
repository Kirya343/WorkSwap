package org.workswap.security.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.workswap.datasource.testers.HttpRequestStatisticsFilter;
import org.workswap.sso.security.config.CorsConfig;
import org.workswap.sso.security.service.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CorsConfig corsConfig;
    private final HttpRequestStatisticsFilter statisticFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // .authorizeHttpRequests(auth -> auth

            //     .requestMatchers("/r/**").permitAll()

            //     // для установки подключения к вебсокету
            //     .requestMatchers("/ws/**").permitAll()

            //     // для вызова методов вебсокета
            //     .requestMatchers( "/app/**").authenticated()

            //     .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            //     .anyRequest().authenticated()
            // )
            .addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class)
            .addFilterBefore(statisticFilter, AuthorizationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .anonymous(anonymous -> anonymous.disable())
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(corsConfig.getDomains());
        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }
}