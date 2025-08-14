package com.sejong.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    // 인증번호 저장용 (실제로는 Redis나 DB 사용 권장)
    private final Map<String, VerificationData> verificationCodes = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // 인증번호 데이터 클래스
    private static class VerificationData {
        String code;
        long expiryTime;
        
        VerificationData(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
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
     * 이메일 첨부 파일 크기 제한 (1MB)
     */
    private static final long MAX_EMAIL_ATTACHMENT_SIZE = 1024 * 1024; // 1MB
    
    /**
     * 인증번호 이메일 발송
     */
    public boolean sendVerificationEmail(String email) {
        try {
            String verificationCode = generateVerificationCode();
            
            // 인증번호 저장 (5분간 유효)
            long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000); // 5분
            verificationCodes.put(email, new VerificationData(verificationCode, expiryTime));
            
            // 5분 후 자동 삭제 스케줄링
            scheduler.schedule(() -> {
                verificationCodes.remove(email);
            }, 5, TimeUnit.MINUTES);
            
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
        VerificationData data = verificationCodes.get(email);
        
        if (data != null && !data.isExpired() && data.code.equals(code)) {
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
    
    /**
     * 이메일 첨부 파일 크기 검증
     */
    public boolean validateEmailAttachmentSize(long fileSize) {
        return fileSize <= MAX_EMAIL_ATTACHMENT_SIZE;
    }
    
    /**
     * 이메일용 파일 크기 제한 메시지 반환
     */
    public String getEmailAttachmentSizeMessage() {
        return "이메일 첨부 파일은 1MB까지만 허용됩니다.";
    }


} 