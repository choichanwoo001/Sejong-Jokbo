package com.sejong.service;

import com.sejong.entity.Admin;
import com.sejong.entity.Jokbo;
import com.sejong.entity.JokboApprovalHistory;
import com.sejong.repository.AdminRepository;
import com.sejong.repository.JokboApprovalHistoryRepository;
import com.sejong.repository.JokboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JokboApprovalHistoryService {

    private final JokboApprovalHistoryRepository historyRepository;
    private final JokboRepository jokboRepository;
    private final AdminRepository adminRepository;

    /**
     * 승인 이력 저장
     */
    @Transactional
    public JokboApprovalHistory save(@org.springframework.lang.NonNull Integer jokboId,
            @org.springframework.lang.NonNull Integer adminId,
            JokboApprovalHistory.ApprovalAction action,
            Jokbo.JokboStatus previousStatus,
            Jokbo.JokboStatus newStatus,
            String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new IllegalArgumentException("족보를 찾을 수 없습니다."));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        JokboApprovalHistory history = new JokboApprovalHistory();
        history.setJokbo(jokbo);
        history.setAdmin(admin);
        history.setAction(action);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setComment(comment);

        return historyRepository.save(history);
    }

    /**
     * 특정 족보의 승인 이력 조회
     */
    public List<JokboApprovalHistory> getHistoryByJokboId(@org.springframework.lang.NonNull Integer jokboId) {
        return historyRepository.findByJokboJokboIdOrderByCreatedAtDesc(jokboId);
    }

    /**
     * 관리자별 승인 이력 조회
     */
    public Page<JokboApprovalHistory> getHistoryByAdminId(@org.springframework.lang.NonNull Integer adminId,
            Pageable pageable) {
        return historyRepository.findByAdminAdminIdOrderByCreatedAtDesc(adminId, pageable);
    }

    /**
     * 전체 승인 이력 조회 (관리자용)
     */
    public Page<JokboApprovalHistory> getAllHistory(Pageable pageable) {
        return historyRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 특정 액션의 이력만 조회
     */
    public Page<JokboApprovalHistory> getHistoryByAction(JokboApprovalHistory.ApprovalAction action,
            Pageable pageable) {
        return historyRepository.findByActionOrderByCreatedAtDesc(action, pageable);
    }

    /**
     * 공개된 승인 이력 조회 (승인, 반려)
     */
    public Page<JokboApprovalHistory> getPublicApprovalHistory(Pageable pageable) {
        return historyRepository.findByActionInOrderByCreatedAtDesc(
                List.of(JokboApprovalHistory.ApprovalAction.승인, JokboApprovalHistory.ApprovalAction.반려),
                pageable);
    }
}