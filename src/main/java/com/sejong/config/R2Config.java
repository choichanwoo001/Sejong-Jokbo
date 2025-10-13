package com.sejong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Cloudflare R2 설정
 * R2는 S3 호환 API를 제공하므로 AWS S3 SDK를 사용합니다
 */
@Configuration
@ConditionalOnProperty(name = "app.storage.type", havingValue = "r2")
public class R2Config {

    @Value("${app.storage.r2.account-id}")
    private String accountId;

    @Value("${app.storage.r2.access-key}")
    private String accessKey;

    @Value("${app.storage.r2.secret-key}")
    private String secretKey;

    @Bean
    public S3Client r2Client() {
        // Cloudflare R2 엔드포인트
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto")) // R2는 자동 리전을 사용
                .build();
    }

    @Bean
    public S3Presigner r2Presigner() {
        // Cloudflare R2 엔드포인트
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto"))
                .build();
    }
}

