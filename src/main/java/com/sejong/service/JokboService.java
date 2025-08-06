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
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));
        
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 파일명 생성 (중복 방지)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
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
} 