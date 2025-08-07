package com.sejong.service;

import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.repository.BookRepository;
import com.sejong.repository.JokboRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JokboService {

    private final JokboRepository jokboRepository;
    private final BookRepository bookRepository;
    
    private static final String UPLOAD_DIR = "uploads/jokbo/";
    
    /**
     * 특정 책의 승인된 족보 목록을 가져옵니다
     */
    public List<Jokbo> getApprovedJokbosByBookId(Integer bookId) {
        return jokboRepository.findApprovedJokbosByBookId(bookId);
    }
    
    /**
     * 특정 책의 모든 족보 목록을 가져옵니다 (관리자용)
     */
    public List<Jokbo> getAllJokbosByBookId(Integer bookId) {
        return jokboRepository.findAllJokbosByBookId(bookId);
    }
    
    /**
     * 텍스트 족보를 등록합니다
     */
    public Jokbo registerTextJokbo(Integer bookId, String uploaderName, String content, String comment) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));
        
        Jokbo jokbo = new Jokbo();
        jokbo.setBook(book);
        jokbo.setUploaderName(uploaderName);
        jokbo.setContentUrl(content); // 텍스트 내용을 URL 필드에 저장
        jokbo.setContentType("text");
        jokbo.setComment(comment);
        jokbo.setStatus(Jokbo.JokboStatus.대기);
        
        return jokboRepository.save(jokbo);
    }
    
    /**
     * 파일 족보를 등록합니다
     */
    public Jokbo registerFileJokbo(Integer bookId, String uploaderName, MultipartFile file, String comment) throws IOException {
        // 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }
        
        // 파일 크기 검사 (10MB 제한)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과합니다.");
        }
        
        // 허용된 파일 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileExtension(fileExtension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (허용: pdf, jpg, jpeg, png, gif, txt)");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));
        
        // 업로드 디렉토리 생성
        Path uploadPath = createUploadDirectory();
        System.out.println("업로드 경로: " + uploadPath.toAbsolutePath());
        
        // 파일명 생성 (중복 방지)
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;
        System.out.println("새 파일명: " + newFilename);
        
        // 파일 저장
        Path filePath = uploadPath.resolve(newFilename);
        System.out.println("저장할 파일 경로: " + filePath.toAbsolutePath());
        
        try {
            Files.copy(file.getInputStream(), filePath);
            System.out.println("파일 저장 성공");
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
            throw new IOException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
        
        Jokbo jokbo = new Jokbo();
        jokbo.setBook(book);
        jokbo.setUploaderName(uploaderName);
        jokbo.setContentUrl(newFilename); // 파일명을 URL 필드에 저장
        jokbo.setContentType("file");
        jokbo.setComment(comment);
        jokbo.setStatus(Jokbo.JokboStatus.대기);
        
        return jokboRepository.save(jokbo);
    }
    
    /**
     * 업로드 디렉토리를 생성합니다
     */
    private Path createUploadDirectory() throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        System.out.println("업로드 디렉토리 경로: " + uploadPath.toAbsolutePath());
        
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                System.out.println("업로드 디렉토리 생성 성공");
            } catch (IOException e) {
                System.err.println("업로드 디렉토리 생성 실패: " + e.getMessage());
                throw new IOException("업로드 디렉토리 생성에 실패했습니다: " + e.getMessage(), e);
            }
        } else {
            System.out.println("업로드 디렉토리가 이미 존재합니다");
        }
        return uploadPath;
    }
    
    /**
     * 파일 확장자를 추출합니다
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 허용된 파일 확장자인지 확인합니다
     */
    private boolean isAllowedFileExtension(String extension) {
        String[] allowedExtensions = {"pdf", "jpg", "jpeg", "png", "gif", "txt"};
        for (String allowed : allowedExtensions) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 족보를 승인합니다
     */
    public Jokbo approveJokbo(Integer jokboId) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new RuntimeException("족보를 찾을 수 없습니다"));
        jokbo.setStatus(Jokbo.JokboStatus.승인);
        return jokboRepository.save(jokbo);
    }
    
    /**
     * 족보를 반려합니다
     */
    public Jokbo rejectJokbo(Integer jokboId) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new RuntimeException("족보를 찾을 수 없습니다"));
        jokbo.setStatus(Jokbo.JokboStatus.반려);
        return jokboRepository.save(jokbo);
    }
    
    /**
     * 파일 경로를 가져옵니다
     */
    public Path getFilePath(String filename) {
        return Paths.get(UPLOAD_DIR, filename);
    }
    
    /**
     * 승인 대기 중인 족보 수를 가져옵니다
     */
    public long getPendingJokbosCount() {
        return jokboRepository.countByStatus(Jokbo.JokboStatus.대기);
    }
    
    /**
     * 승인 대기 중인 족보 목록을 가져옵니다
     */
    public List<Jokbo> getPendingJokbos() {
        return jokboRepository.findByStatusOrderByCreatedAtDesc(Jokbo.JokboStatus.대기);
    }
} 