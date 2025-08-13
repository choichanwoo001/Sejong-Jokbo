package com.sejong.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 파일 저장 서비스 인터페이스
 * 로컬 저장소와 클라우드 저장소를 추상화
 */
public interface FileStorageService {
    
    /**
     * 파일을 업로드합니다
     */
    String uploadFile(MultipartFile file, String filename) throws IOException;
    
    /**
     * 파일을 다운로드합니다
     */
    Resource downloadFile(String filename) throws IOException;
    
    /**
     * 파일의 공개 URL을 반환합니다
     */
    String getPublicUrl(String filename);
    
    /**
     * 파일을 삭제합니다
     */
    boolean deleteFile(String filename);
    
    /**
     * 파일이 존재하는지 확인합니다
     */
    boolean fileExists(String filename);
}
