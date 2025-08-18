package com.sejong.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Storage 관련 설정 클래스
 */
@Configuration
@Slf4j
public class StorageConfig {

    /**
     * Google Cloud Storage Bean (프로덕션 환경에서만 활성화)
     */
    @Bean
    @ConditionalOnProperty(name = "app.development.use-local-storage", havingValue = "false")
    public Storage googleCloudStorage(
            @Value("${spring.cloud.gcp.storage.project-id}") String projectId,
            @Value("${spring.cloud.gcp.storage.credentials.location:#{null}}") String credentialsLocation) {
        
        try {
            StorageOptions.Builder storageOptionsBuilder = StorageOptions.newBuilder()
                    .setProjectId(projectId);

            // 자격 증명 파일이 지정된 경우
            if (credentialsLocation != null && !credentialsLocation.isEmpty()) {
                if (credentialsLocation.startsWith("classpath:")) {
                    String resourcePath = credentialsLocation.substring("classpath:".length());
                    ClassPathResource resource = new ClassPathResource(resourcePath);
                    GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
                    storageOptionsBuilder.setCredentials(credentials);
                    log.info("Google Cloud Storage 자격 증명 파일 로드 성공: {}", credentialsLocation);
                }
            } else {
                // 환경 변수나 메타데이터 서버에서 자격 증명 자동 감지
                log.info("Google Cloud Storage 기본 자격 증명 사용");
            }

            Storage storage = storageOptionsBuilder.build().getService();
            log.info("Google Cloud Storage 초기화 완료");
            return storage;
            
        } catch (IOException e) {
            log.error("Google Cloud Storage 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("Google Cloud Storage 설정 오류", e);
        }
    }
}
