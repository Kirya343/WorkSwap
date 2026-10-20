package org.workswap.storage.config;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@Profile("server")
@ConditionalOnProperty(
    name = "storage.s3.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class S3Config {
    
    @Bean
    public S3Client s3Client(S3Properties config) {
        return S3Client.builder()
                .endpointOverride(URI.create(config.endpoint()))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.accessKey(), config.secretKey())
                    )
                )
                .region(Region.of("eeur"))
                .build();
    }
}
