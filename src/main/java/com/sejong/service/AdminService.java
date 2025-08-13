package com.sejong.service;

import com.sejong.entity.Admin;
import com.sejong.entity.Jokbo;
import com.sejong.entity.JokboApprovalHistory;
import com.sejong.repository.AdminRepository;
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
        
        // 승인 이력 저장
        JokboApprovalHistory history = new JokboApprovalHistory();
        history.setJokbo(jokbo);
        history.setAdmin(admin);
        history.setAction(JokboApprovalHistory.ApprovalAction.승인);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(Jokbo.JokboStatus.승인);
        history.setComment(comment);
        jokboApprovalHistoryRepository.save(history);
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
     * 모든 승인 이력을 페이징으로 조회합니다
     */
    public Page<JokboApprovalHistory> getAllApprovalHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jokboApprovalHistoryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * 특정 관리자의 승인 처리 이력을 조회합니다
     */
    public Page<JokboApprovalHistory> getAdminApprovalHistory(Integer adminId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jokboApprovalHistoryRepository.findByAdminAdminIdOrderByCreatedAtDesc(adminId, pageable);
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
