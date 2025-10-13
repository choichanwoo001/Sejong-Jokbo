package com.sejong.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Cloudflare R2를 사용한 파일 저장 서비스
 * R2는 S3 호환 API를 제공하므로 AWS S3 SDK를 사용합니다
 */
@Service("r2StorageService")
@ConditionalOnProperty(name = "app.storage.type", havingValue = "r2")
@RequiredArgsConstructor
@Slf4j
public class R2StorageService implements FileStorageService {

    private final S3Client r2Client;
    private final S3Presigner r2Presigner;

    @Value("${app.storage.bucket-name}")
    private String bucketName;

    @Value("${app.storage.r2.public-url:}")
    private String publicUrl;

    /**
     * 파일을 R2에 업로드합니다
     */
    @Override
    public String uploadFile(MultipartFile file, String filename) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            r2Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("R2 파일 업로드 성공: {}", filename);
            
            return filename;
        } catch (Exception e) {
            log.error("R2 파일 업로드 실패: {}", e.getMessage(), e);
            throw new IOException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일을 R2에서 다운로드합니다
     */
    @Override
    public Resource downloadFile(String filename) throws IOException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            byte[] data = r2Client.getObjectAsBytes(getObjectRequest).asByteArray();
            log.info("R2 파일 다운로드 성공: {}", filename);
            
            return new ByteArrayResource(data);
        } catch (NoSuchKeyException e) {
            log.error("R2 파일을 찾을 수 없습니다: {}", filename);
            throw new IOException("파일을 찾을 수 없습니다: " + filename);
        } catch (Exception e) {
            log.error("R2 파일 다운로드 실패: {}", e.getMessage(), e);
            throw new IOException("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일의 공개 URL을 반환합니다
     * R2 Custom Domain을 설정한 경우 해당 도메인의 URL을 반환합니다
     */
    @Override
    public String getPublicUrl(String filename) {
        if (publicUrl != null && !publicUrl.isEmpty()) {
            // Custom Domain이 설정된 경우
            return publicUrl + (publicUrl.endsWith("/") ? "" : "/") + filename;
        }
        
        // Custom Domain이 없으면 presigned URL 반환 (1시간 유효)
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = r2Presigner.presignGetObject(presignRequest);
            
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("R2 URL 생성 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 파일을 삭제합니다
     */
    @Override
    public boolean deleteFile(String filename) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            r2Client.deleteObject(deleteObjectRequest);
            log.info("R2 파일 삭제 성공: {}", filename);
            return true;
        } catch (Exception e) {
            log.error("R2 파일 삭제 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 파일이 존재하는지 확인합니다
     */
    @Override
    public boolean fileExists(String filename) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            r2Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("R2 파일 존재 확인 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 파일의 로컬 경로를 반환합니다 (R2의 경우 지원하지 않음)
     */
    @Override
    public Path getFilePath(String filename) {
        throw new UnsupportedOperationException("R2 Storage는 로컬 파일 경로를 지원하지 않습니다. downloadFile()을 사용하세요.");
    }
}

