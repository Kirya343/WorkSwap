package org.workswap.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "s3")
public record S3Properties(
    @NotBlank String endpoint,
    @NotBlank String bucket,
    @NotBlank String accessKey,
    @NotBlank String secretKey
) {
}