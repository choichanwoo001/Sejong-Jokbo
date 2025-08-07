package com.sejong.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Google Cloud Storage를 사용한 파일 저장 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final Storage storage;

    @Value("${app.storage.bucket-name}")
    private String bucketName;

    /**
     * 파일을 Google Cloud Storage에 업로드합니다
     */
    public String uploadFile(MultipartFile file, String filename) throws IOException {
        try {
            BlobId blobId = BlobId.of(bucketName, filename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            Blob blob = storage.create(blobInfo, file.getBytes());
            log.info("파일 업로드 성공: {}", blob.getName());
            
            return blob.getName();
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new IOException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일을 Google Cloud Storage에서 다운로드합니다
     */
    public Resource downloadFile(String filename) throws IOException {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, filename));
            if (blob == null) {
                throw new IOException("파일을 찾을 수 없습니다: " + filename);
            }

            // 서명된 URL 생성 (1시간 유효)
            URL signedUrl = blob.signUrl(1, TimeUnit.HOURS);
            return new UrlResource(signedUrl);
        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", e.getMessage());
            throw new IOException("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일의 공개 URL을 생성합니다
     */
    public String getPublicUrl(String filename) {
        Blob blob = storage.get(BlobId.of(bucketName, filename));
        if (blob == null) {
            return null;
        }
        return blob.getMediaLink();
    }

    /**
     * 파일을 삭제합니다
     */
    public boolean deleteFile(String filename) {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, filename));
            if (blob != null) {
                boolean deleted = blob.delete();
                log.info("파일 삭제 성공: {}", filename);
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 파일이 존재하는지 확인합니다
     */
    public boolean fileExists(String filename) {
        Blob blob = storage.get(BlobId.of(bucketName, filename));
        return blob != null && blob.exists();
    }
}
