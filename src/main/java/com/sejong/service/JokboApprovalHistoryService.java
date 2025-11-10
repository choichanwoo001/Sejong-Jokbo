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

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JokboApprovalHistoryService {
    
    private final JokboApprovalHistoryRepository historyRepository;
    private final JokboRepository jokboRepository;
    private final AdminRepository adminRepository;
    
    /**
     * 족보 승인 이력 기록
     */
    @Transactional
    public JokboApprovalHistory recordApprovalHistory(Integer jokboId, Integer adminId, 
                                                     JokboApprovalHistory.ApprovalAction action,
                                                     Jokbo.JokboStatus previousStatus,
                                                     Jokbo.JokboStatus newStatus,
                                                     String comment) {
        Jokbo jokbo = jokboRepository.findById(jokboId)
                .orElseThrow(() -> new EntityNotFoundException("족보를 찾을 수 없습니다."));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다."));
        
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
    public List<JokboApprovalHistory> getHistoryByJokboId(Integer jokboId) {
        return historyRepository.findByJokboJokboIdOrderByCreatedAtDesc(jokboId);
    }
    
    /**
     * 관리자별 승인 이력 조회
     */
    public Page<JokboApprovalHistory> getHistoryByAdminId(Integer adminId, Pageable pageable) {
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
    public Page<JokboApprovalHistory> getHistoryByAction(JokboApprovalHistory.ApprovalAction action, Pageable pageable) {
        return historyRepository.findByActionOrderByCreatedAtDesc(action, pageable);
    }
}