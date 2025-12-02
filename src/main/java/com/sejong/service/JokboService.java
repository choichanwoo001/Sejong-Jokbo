package com.sejong.service;

import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.repository.BookRepository;
import com.sejong.repository.JokboRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JokboService {

    private final JokboRepository jokboRepository;
    private final BookRepository bookRepository;
    private final PdfService pdfService;
    private final FileStorageService fileStorageService;
    private final SseService sseService;

    /**
     * 특정 책의 승인된 족보 목록을 페이징하여 가져옵니다 (5개씩)
     */
    public Page<Jokbo> getApprovedJokbosByBookId(@org.springframework.lang.NonNull Integer bookId, int page) {
        Pageable pageable = PageRequest.of(page, 5); // 5개씩 페이징
        return jokboRepository.findApprovedJokbosByBookId(bookId, pageable);
    }

    /**
     * 특정 책의 모든 족보 목록을 페이징하여 가져옵니다 (관리자용, 5개씩)
     */
    public Page<Jokbo> getAllJokbosByBookId(@org.springframework.lang.NonNull Integer bookId, int page) {
        Pageable pageable = PageRequest.of(page, 5); // 5개씩 페이징
        return jokboRepository.findAllJokbosByBookId(bookId, pageable);
    }

    /**
     * 특정 책의 승인된 족보 목록을 가져옵니다 (페이징 없음)
     */
    public List<Jokbo> getApprovedJokbosByBookId(@org.springframework.lang.NonNull Integer bookId) {
        return jokboRepository.findApprovedJokbosByBookId(bookId);
    }

    /**
     * 특정 책의 모든 족보 목록을 가져옵니다 (관리자용, 페이징 없음)
     */
    public List<Jokbo> getAllJokbosByBookId(@org.springframework.lang.NonNull Integer bookId) {
        return jokboRepository.findAllJokbosByBookId(bookId);
    }

    /**
     * 텍스트 족보를 등록합니다
     */
    public Jokbo registerTextJokbo(@org.springframework.lang.NonNull Integer bookId, String uploaderName,
            String content, String comment) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));

        log.info("텍스트 족보 등록 시작 - 책 ID: {}, 등록자: {}, 내용 길이: {}", bookId, uploaderName, content.length());

        Jokbo jokbo = new Jokbo();
        jokbo.setBook(book);
        jokbo.setUploaderName(uploaderName);
        jokbo.setContent(content); // 텍스트 내용만 RDB에 저장
        jokbo.setContentUrl(null); // 텍스트 족보는 파일 저장하지 않음
        jokbo.setContentType("text");
        jokbo.setComment(comment);
        jokbo.setStatus(Jokbo.JokboStatus.대기);

        Jokbo savedJokbo = jokboRepository.save(jokbo);
        log.info("텍스트 족보 저장 완료");

        // 관리자에게 새로운 족보 요청 알림 전송
        sseService.sendNewJokboRequestNotification(book.getTitle(), uploaderName);

        return savedJokbo;
    }

    /**
     * 파일 족보를 등록합니다
     */
    public Jokbo registerFileJokbo(@org.springframework.lang.NonNull Integer bookId, String uploaderName,
            MultipartFile file, String comment)
            throws IOException {
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

        // 파일명 생성 (중복 방지)
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;
        log.info("새 파일명: {}", newFilename);

        // 환경에 따라 자동으로 선택되는 파일 저장 서비스 사용
        try {
            String uploadedFilename = fileStorageService.uploadFile(file, newFilename);
            log.info("파일 업로드 성공: {}", uploadedFilename);
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new IOException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

        Jokbo jokbo = new Jokbo();
        jokbo.setBook(book);
        jokbo.setUploaderName(uploaderName);
        jokbo.setContentUrl(newFilename); // 파일명을 URL 필드에 저장
        jokbo.setContentType("file");
        jokbo.setComment(comment);
        jokbo.setStatus(Jokbo.JokboStatus.대기);

        Jokbo savedJokbo = jokboRepository.save(jokbo);

        // 관리자에게 새로운 족보 요청 알림 전송
        sseService.sendNewJokboRequestNotification(book.getTitle(), uploaderName);

        return savedJokbo;
    }

    /**
     * 파일 경로를 가져옵니다 (환경에 따라 자동 선택)
     */
    public Path getFilePath(String filename) {
        // FileStorageService 인터페이스를 통해 환경에 맞는 경로 반환
        return fileStorageService.getFilePath(filename);
    }

    /**
     * FileStorageService를 반환합니다
     */
    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    /**
     * 승인 대기 중인 족보 수를 가져옵니다
     */
    public long getPendingJokbosCount() {
        return jokboRepository.countByStatus(Jokbo.JokboStatus.대기);
    }

    /**
     * 승인 대기 중인 족보 목록을 페이징하여 가져옵니다 (5개씩)
     */
    public Page<Jokbo> getPendingJokbos(int page) {
        Pageable pageable = PageRequest.of(page, 5); // 5개씩 페이징
        return jokboRepository.findByStatusOrderByCreatedAtDesc(Jokbo.JokboStatus.대기, pageable);
    }

    /**
     * 승인 대기 중인 족보 목록을 가져옵니다 (페이징 없음)
     */
    public List<Jokbo> getPendingJokbos() {
        return jokboRepository.findByStatusOrderByCreatedAtDesc(Jokbo.JokboStatus.대기);
    }

    /**
     * ID로 족보를 가져옵니다
     */
    public Jokbo getJokboById(@org.springframework.lang.NonNull Integer jokboId) {
        return jokboRepository.findById(jokboId)
                .orElseThrow(() -> new RuntimeException("족보를 찾을 수 없습니다"));
    }

    /**
     * 텍스트 족보를 PDF 바이트 배열로 변환합니다 (실시간 변환)
     */
    public byte[] getTextJokboAsPdf(@org.springframework.lang.NonNull Integer jokboId) throws Exception {
        Jokbo jokbo = getJokboById(jokboId);

        if (!"text".equals(jokbo.getContentType())) {
            throw new IllegalArgumentException("텍스트 족보가 아닙니다.");
        }

        if (jokbo.getContent() == null || jokbo.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("족보 내용이 없습니다.");
        }

        return pdfService.createPdfBytesFromText(jokbo.getBook().getTitle(), jokbo.getContent(),
                jokbo.getUploaderName());
    }

    /**
     * 상태별 족보 목록을 페이징하여 가져옵니다
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public Page<Jokbo> getJokbosByStatus(Jokbo.JokboStatus status, int page, int size) {
        // JOIN FETCH로 Book 정보까지 함께 로딩
        List<Jokbo> allJokbos = jokboRepository.findByStatusWithBookOrderByCreatedAtDesc(status);

        // 메모리에서 페이징 처리
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allJokbos.size());

        List<Jokbo> pageContent = start >= allJokbos.size() ? new ArrayList<>() : allJokbos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allJokbos.size());
    }

    /**
     * 모든 족보 목록을 페이징하여 가져옵니다 (관리자용)
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public Page<Jokbo> getAllJokbos(int page, int size) {
        // JOIN FETCH로 Book 정보까지 함께 로딩
        List<Jokbo> allJokbos = jokboRepository.findAllWithBookOrderByCreatedAtDesc();

        // 메모리에서 페이징 처리
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allJokbos.size());

        List<Jokbo> pageContent = start >= allJokbos.size() ? new ArrayList<>() : allJokbos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allJokbos.size());
    }

    /**
     * 파일 확장자를 추출합니다
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 허용된 파일 확장자인지 확인합니다
     */
    private boolean isAllowedFileExtension(String extension) {
        return List.of("pdf", "jpg", "jpeg", "png", "gif", "txt").contains(extension);
    }

    /**
     * 파일명으로 족보를 찾습니다 (contentUrl 기준)
     */
    public Jokbo getJokboByContentUrl(String contentUrl) {
        return jokboRepository.findByContentUrl(contentUrl).orElse(null);
    }

    /**
     * 다운로드 카운트를 증가시킵니다
     */
    @Transactional
    public void increaseDownloadCount(@org.springframework.lang.NonNull Integer jokboId) {
        Jokbo jokbo = jokboRepository.findById(jokboId).orElse(null);
        if (jokbo != null) {
            jokbo.setDownloadCount(jokbo.getDownloadCount() + 1);
            jokboRepository.save(jokbo);
        }
    }

    /**
     * 총 다운로드 수를 반환합니다
     */
    public long getTotalDownloadCount() {
        Long count = jokboRepository.sumDownloadCount();
        return count != null ? count : 0L;
    }

    /**
     * 족보를 삭제합니다 (파일 포함)
     */
    @Transactional
    public void deleteJokbo(@org.springframework.lang.NonNull Integer jokboId) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));

        // 파일 족보인 경우 실제 파일 삭제
        if ("file".equals(jokbo.getContentType()) && jokbo.getContentUrl() != null) {
            fileStorageService.deleteFile(jokbo.getContentUrl());
        }

        jokboRepository.delete(jokbo);
        log.info("족보 삭제 완료: ID {}", jokboId);
    }
}