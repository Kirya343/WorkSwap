package org.workswap.security.config;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCache permissionsCache = new CaffeineCache(
            "user-permissions",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(15, TimeUnit.MINUTES)
                    .maximumSize(100_000)
                    .build()
            )
        );

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Objects.requireNonNull(List.of(permissionsCache)));
        return manager;
    }
}