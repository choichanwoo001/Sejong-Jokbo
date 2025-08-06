package com.sejong.service;

import com.sejong.entity.Admin;
import com.sejong.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminRepository adminRepository;
    
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
