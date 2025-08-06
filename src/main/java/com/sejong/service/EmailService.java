package com.sejong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    // 인증번호 저장용 (실제로는 Redis나 DB 사용 권장)
    private final Map<String, String> verificationCodes = new HashMap<>();
    
    /**
     * 인증번호 생성
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }
    
    /**
     * 인증번호 이메일 발송
     */
    public boolean sendVerificationEmail(String email) {
        try {
            String verificationCode = generateVerificationCode();
            
            // 인증번호 저장 (5분간 유효)
            verificationCodes.put(email, verificationCode);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[세종족보] 이메일 인증번호");
            message.setText("안녕하세요!\n\n" +
                    "세종족보 이메일 인증번호는 [" + verificationCode + "] 입니다.\n" +
                    "이 인증번호는 5분간 유효합니다.\n\n" +
                    "감사합니다.");
            
            mailSender.send(message);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 인증번호 확인
     */
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        
        if (savedCode != null && savedCode.equals(code)) {
            // 인증 완료 후 저장된 코드 삭제
            verificationCodes.remove(email);
            return true;
        }
        
        return false;
    }
    
    /**
     * 인증번호 만료 처리 (5분 후 자동 삭제)
     */
    public void removeExpiredCode(String email) {
        verificationCodes.remove(email);
    }
} 