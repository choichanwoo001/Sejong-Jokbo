package com.sejong.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 로컬 파일 시스템을 사용한 파일 저장 서비스
 */
@Service("localStorageService")
@ConditionalOnProperty(name = "app.development.use-local-storage", havingValue = "true", matchIfMissing = true)
@Slf4j
public class LocalStorageService implements FileStorageService {

    private final Path uploadPath = Paths.get("uploads/jokbo/");

    public LocalStorageService() {
        try {
            // 업로드 디렉토리 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("로컬 업로드 디렉토리가 생성되었습니다: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("로컬 업로드 디렉토리 생성에 실패했습니다: {}", e.getMessage());
        }
    }

    /**
     * 파일을 로컬 파일 시스템에 업로드합니다
     */
    public String uploadFile(MultipartFile file, String filename) throws IOException {
        try {
            Path targetPath = uploadPath.resolve(filename);
            
            // 상위 디렉토리가 없으면 생성
            Files.createDirectories(targetPath.getParent());
            
            // 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("로컬 파일 업로드 성공: {}", targetPath.toAbsolutePath());
            
            return filename;
        } catch (Exception e) {
            log.error("로컬 파일 업로드 실패: {}", e.getMessage());
            throw new IOException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일을 로컬 파일 시스템에서 다운로드합니다
     */
    public Resource downloadFile(String filename) throws IOException {
        try {
            Path filePath = uploadPath.resolve(filename);
            if (!Files.exists(filePath)) {
                throw new IOException("파일을 찾을 수 없습니다: " + filename);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("파일을 읽을 수 없습니다: " + filename);
            }
        } catch (Exception e) {
            log.error("로컬 파일 다운로드 실패: {}", e.getMessage());
            throw new IOException("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일의 로컬 URL을 생성합니다
     */
    public String getPublicUrl(String filename) {
        Path filePath = uploadPath.resolve(filename);
        if (Files.exists(filePath)) {
            return "/uploads/jokbo/" + filename;
        }
        return null;
    }

    /**
     * 파일을 삭제합니다
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("로컬 파일 삭제 성공: {}", filename);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("로컬 파일 삭제 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 파일이 존재하는지 확인합니다
     */
    public boolean fileExists(String filename) {
        Path filePath = uploadPath.resolve(filename);
        return Files.exists(filePath);
    }
}
