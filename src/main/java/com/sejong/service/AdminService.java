package com.sejong.service;

import com.sejong.entity.Admin;
import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.entity.JokboApprovalHistory;
import com.sejong.repository.AdminRepository;
import com.sejong.repository.JokboApprovalHistoryRepository;
import com.sejong.repository.JokboRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final JokboRepository jokboRepository;
    private final JokboApprovalHistoryRepository jokboApprovalHistoryRepository;
    private final BookService bookService;
    private final SseService sseService;
    private final JokboService jokboService;
    private static final String DEFAULT_ADMIN_NAME = "기본 관리자";
    private static final String DEFAULT_ADMIN_PASSWORD = "";

    /**
     * 기본 관리자 계정을 조회하거나 없으면 생성합니다.
     */
    @Transactional
    public Admin getOrCreateDefaultAdmin() {
        return adminRepository.findByAdminName(DEFAULT_ADMIN_NAME)
                .orElseGet(() -> {
                    Admin admin = new Admin();
                    admin.setAdminName(DEFAULT_ADMIN_NAME);
                    admin.setPassword(DEFAULT_ADMIN_PASSWORD);
                    return adminRepository.save(admin);
                });
    }

    /**
     * 족보를 승인합니다
     */
    @Transactional
    public void approveJokbo(@org.springframework.lang.NonNull Integer jokboId, String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));

        Jokbo.JokboStatus previousStatus = jokbo.getStatus();
        jokbo.setStatus(Jokbo.JokboStatus.승인);
        jokboRepository.save(jokbo);

        // 승인된 경우 해당 책의 jokboCount 업데이트
        if (previousStatus != Jokbo.JokboStatus.승인) {
            bookService.updateJokboCount(java.util.Objects.requireNonNull(jokbo.getBook().getBookId()));
        }

        // 승인 이력 저장
        saveApprovalHistory(jokbo, JokboApprovalHistory.ApprovalAction.승인, previousStatus, Jokbo.JokboStatus.승인,
                comment);

        // 사용자에게 족보 승인 알림 전송
        try {
            String bookTitle = jokbo.getBook().getTitle();
            String jokboTitle = jokbo.getUploaderName() + "님의 족보";
            sseService.sendJokboApprovalNotification(bookTitle, jokboTitle);
        } catch (Exception e) {
            // SSE 알림 전송 실패는 로그만 남기고 승인 프로세스는 계속 진행
            log.error("SSE 알림 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 족보를 반려합니다
     */
    @Transactional
    public void rejectJokbo(@org.springframework.lang.NonNull Integer jokboId, String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));

        Jokbo.JokboStatus previousStatus = jokbo.getStatus();
        jokbo.setStatus(Jokbo.JokboStatus.반려);
        jokboRepository.save(jokbo);

        // 이전에 승인되었던 족보를 반려하는 경우 jokboCount 업데이트
        if (previousStatus == Jokbo.JokboStatus.승인) {
            bookService.updateJokboCount(java.util.Objects.requireNonNull(jokbo.getBook().getBookId()));
        }

        // 반려 이력 저장
        saveApprovalHistory(jokbo, JokboApprovalHistory.ApprovalAction.반려, previousStatus, Jokbo.JokboStatus.반려,
                comment);
    }

    /**
     * 족보 승인을 취소합니다 (승인 -> 대기)
     */
    @Transactional
    public void cancelApproval(@org.springframework.lang.NonNull Integer jokboId, String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));

        if (jokbo.getStatus() != Jokbo.JokboStatus.승인) {
            throw new IllegalStateException("승인된 족보만 승인 취소할 수 있습니다.");
        }

        Jokbo.JokboStatus previousStatus = jokbo.getStatus();
        jokbo.setStatus(Jokbo.JokboStatus.대기);
        jokboRepository.save(jokbo);

        // 승인 취소 시 jokboCount 업데이트
        bookService.updateJokboCount(java.util.Objects.requireNonNull(jokbo.getBook().getBookId()));

        // 승인 취소 이력 저장
        saveApprovalHistory(jokbo, JokboApprovalHistory.ApprovalAction.승인취소, previousStatus, Jokbo.JokboStatus.대기,
                comment);
    }

    /**
     * 승인 이력을 저장합니다 (공통 메서드)
     */
    private void saveApprovalHistory(Jokbo jokbo, JokboApprovalHistory.ApprovalAction action,
            Jokbo.JokboStatus previousStatus, Jokbo.JokboStatus newStatus, String comment) {
        Admin admin = getOrCreateDefaultAdmin();
        JokboApprovalHistory history = new JokboApprovalHistory();
        history.setJokbo(jokbo);
        history.setAdmin(admin);
        history.setAction(action);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
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

        return jokboApprovalHistoryRepository.findByJokboIdWithFilters(jokboId, actionEnum, previousStatusEnum,
                newStatusEnum, pageable);
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

        return jokboApprovalHistoryRepository.findAllWithFilters(actionEnum, previousStatusEnum, newStatusEnum,
                pageable);
    }

    /**
     * 특정 관리자의 승인 처리 이력을 조회합니다
     */
    public Page<JokboApprovalHistory> getAdminApprovalHistory(Integer adminId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jokboApprovalHistoryRepository.findByAdminAdminIdOrderByCreatedAtDesc(adminId, pageable);
    }

    /**
     * 족보를 삭제합니다 (반려 또는 대기 상태인 경우만 가능)
     */
    @Transactional
    public void deleteJokbo(@org.springframework.lang.NonNull Integer jokboId) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 족보입니다."));

        // 상태 확인: 반려 또는 대기 상태만 삭제 가능
        if (jokbo.getStatus() == Jokbo.JokboStatus.승인) {
            throw new IllegalStateException("승인된 족보는 삭제할 수 없습니다. 먼저 승인 취소를 해주세요.");
        }

        // 연관된 승인 이력 삭제
        List<JokboApprovalHistory> histories = jokboApprovalHistoryRepository
                .findByJokboJokboIdOrderByCreatedAtDesc(jokboId);
        jokboApprovalHistoryRepository.deleteAll(histories);

        // 족보 삭제 (파일 포함)
        jokboService.deleteJokbo(jokboId);
    }
}
