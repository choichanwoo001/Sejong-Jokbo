package com.sejong.service;

import com.sejong.entity.Admin;
import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.entity.JokboApprovalHistory;
import com.sejong.repository.AdminRepository;
import com.sejong.repository.BookRepository;
import com.sejong.repository.JokboApprovalHistoryRepository;
import com.sejong.repository.JokboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final JokboRepository jokboRepository;
    private final JokboApprovalHistoryRepository jokboApprovalHistoryRepository;
    private final BookRepository bookRepository;
    private final SseService sseService;
    
    /**
     * 관리자 로그인을 처리합니다
     */
    public Admin login(String adminName, String password) {
        String hashedPassword = hashPassword(password);
        return adminRepository.findByAdminNameAndPassword(adminName, hashedPassword)
                .orElse(null);
    }
    
    /**
     * 관리자 ID로 관리자를 조회합니다
     */
    public Admin getAdminById(Integer adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }
    
    /**
     * 족보를 승인합니다
     */
    @Transactional
    public void approveJokbo(Integer jokboId, Integer adminId, String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
        
        Jokbo.JokboStatus previousStatus = jokbo.getStatus();
        jokbo.setStatus(Jokbo.JokboStatus.승인);
        jokboRepository.save(jokbo);
        
        // 승인된 경우 해당 책의 jokboCount 업데이트
        if (previousStatus != Jokbo.JokboStatus.승인) {
            updateBookJokboCount(jokbo.getBook().getBookId());
        }
        
        // 승인 이력 저장
        JokboApprovalHistory history = new JokboApprovalHistory();
        history.setJokbo(jokbo);
        history.setAdmin(admin);
        history.setAction(JokboApprovalHistory.ApprovalAction.승인);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(Jokbo.JokboStatus.승인);
        history.setComment(comment);
        jokboApprovalHistoryRepository.save(history);
        
        // 사용자에게 족보 승인 알림 전송
        try {
            String bookTitle = jokbo.getBook().getTitle();
            String jokboTitle = jokbo.getUploaderName() + "님의 족보";
            sseService.sendJokboApprovalNotification(bookTitle, jokboTitle);
        } catch (Exception e) {
            // SSE 알림 전송 실패는 로그만 남기고 승인 프로세스는 계속 진행
            System.err.println("SSE 알림 전송 실패: " + e.getMessage());
        }
    }
    
    /**
     * 족보를 반려합니다
     */
    @Transactional
    public void rejectJokbo(Integer jokboId, Integer adminId, String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
        
        Jokbo.JokboStatus previousStatus = jokbo.getStatus();
        jokbo.setStatus(Jokbo.JokboStatus.반려);
        jokboRepository.save(jokbo);
        
        // 이전에 승인되었던 족보를 반려하는 경우 jokboCount 업데이트
        if (previousStatus == Jokbo.JokboStatus.승인) {
            updateBookJokboCount(jokbo.getBook().getBookId());
        }
        
        // 반려 이력 저장
        JokboApprovalHistory history = new JokboApprovalHistory();
        history.setJokbo(jokbo);
        history.setAdmin(admin);
        history.setAction(JokboApprovalHistory.ApprovalAction.반려);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(Jokbo.JokboStatus.반려);
        history.setComment(comment);
        jokboApprovalHistoryRepository.save(history);
    }
    
    /**
     * 족보 승인을 취소합니다 (승인 -> 대기)
     */
    @Transactional
    public void cancelApproval(Integer jokboId, Integer adminId, String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
        
        if (jokbo.getStatus() != Jokbo.JokboStatus.승인) {
            throw new IllegalStateException("승인된 족보만 승인 취소할 수 있습니다.");
        }
        
        Jokbo.JokboStatus previousStatus = jokbo.getStatus();
        jokbo.setStatus(Jokbo.JokboStatus.대기);
        jokboRepository.save(jokbo);
        
        // 승인 취소 시 jokboCount 업데이트
        updateBookJokboCount(jokbo.getBook().getBookId());
        
        // 승인 취소 이력 저장
        JokboApprovalHistory history = new JokboApprovalHistory();
        history.setJokbo(jokbo);
        history.setAdmin(admin);
        history.setAction(JokboApprovalHistory.ApprovalAction.승인취소);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(Jokbo.JokboStatus.대기);
        history.setComment(comment);
        jokboApprovalHistoryRepository.save(history);
    }
    
    /**
     * 특정 족보의 승인 이력을 조회합니다
     */
    public List<JokboApprovalHistory> getJokboApprovalHistory(Integer jokboId) {
        return jokboApprovalHistoryRepository.findByJokboJokboIdOrderByCreatedAtDesc(jokboId);
    }
    
    /**
     * 특정 족보의 승인 이력을 필터링과 페이징으로 조회합니다
     */
    public Page<JokboApprovalHistory> getJokboApprovalHistoryWithFilters(Integer jokboId, int page, int size, 
                                                                        String action, String previousStatus, String newStatus) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 문자열을 enum으로 변환
        JokboApprovalHistory.ApprovalAction actionEnum = null;
        if (action != null && !action.isEmpty()) {
            try {
                actionEnum = JokboApprovalHistory.ApprovalAction.valueOf(action);
            } catch (IllegalArgumentException e) {
                // 잘못된 액션 값은 무시
            }
        }
        
        Jokbo.JokboStatus previousStatusEnum = null;
        if (previousStatus != null && !previousStatus.isEmpty()) {
            try {
                previousStatusEnum = Jokbo.JokboStatus.valueOf(previousStatus);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값은 무시
            }
        }
        
        Jokbo.JokboStatus newStatusEnum = null;
        if (newStatus != null && !newStatus.isEmpty()) {
            try {
                newStatusEnum = Jokbo.JokboStatus.valueOf(newStatus);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값은 무시
            }
        }
        
        return jokboApprovalHistoryRepository.findByJokboIdWithFilters(jokboId, actionEnum, previousStatusEnum, newStatusEnum, pageable);
    }
    
    /**
     * 모든 승인 이력을 페이징으로 조회합니다
     */
    public Page<JokboApprovalHistory> getAllApprovalHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jokboApprovalHistoryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * 모든 승인 이력을 필터링과 페이징으로 조회합니다
     */
    public Page<JokboApprovalHistory> getAllApprovalHistoryWithFilters(int page, int size, 
                                                                      String action, String previousStatus, String newStatus) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 문자열을 enum으로 변환
        JokboApprovalHistory.ApprovalAction actionEnum = null;
        if (action != null && !action.isEmpty()) {
            try {
                actionEnum = JokboApprovalHistory.ApprovalAction.valueOf(action);
            } catch (IllegalArgumentException e) {
                // 잘못된 액션 값은 무시
            }
        }
        
        Jokbo.JokboStatus previousStatusEnum = null;
        if (previousStatus != null && !previousStatus.isEmpty()) {
            try {
                previousStatusEnum = Jokbo.JokboStatus.valueOf(previousStatus);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값은 무시
            }
        }
        
        Jokbo.JokboStatus newStatusEnum = null;
        if (newStatus != null && !newStatus.isEmpty()) {
            try {
                newStatusEnum = Jokbo.JokboStatus.valueOf(newStatus);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값은 무시
            }
        }
        
        return jokboApprovalHistoryRepository.findAllWithFilters(actionEnum, previousStatusEnum, newStatusEnum, pageable);
    }
    
    /**
     * 특정 관리자의 승인 처리 이력을 조회합니다
     */
    public Page<JokboApprovalHistory> getAdminApprovalHistory(Integer adminId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jokboApprovalHistoryRepository.findByAdminAdminIdOrderByCreatedAtDesc(adminId, pageable);
    }
    
    /**
     * 책의 승인된 족보 수를 업데이트합니다
     */
    private void updateBookJokboCount(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        
        // 해당 책의 승인된 족보 수를 실시간으로 계산
        long approvedCount = jokboRepository.countByBookIdAndStatus(bookId, Jokbo.JokboStatus.승인);
        book.setJokboCount((int) approvedCount);
        bookRepository.save(book);
    }
    
    /**
     * 비밀번호를 해시화합니다
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해시 알고리즘 오류", e);
        }
    }
}
