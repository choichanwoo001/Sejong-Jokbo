package com.sejong.controller.api;

import com.sejong.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "이메일", description = "이메일 인증 관련 API")
public class EmailRestController {
    
    private final EmailService emailService;
    
    /**
     * 인증번호 발송
     */
    @Operation(summary = "인증번호 발송", description = "이메일로 인증번호를 발송합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "발송 성공/실패 응답"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@Parameter(description = "이메일 주소가 포함된 요청 본문") @RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();
        
        if (email == null || email.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "이메일 주소를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            boolean sent = emailService.sendVerificationEmail(email);
            
            if (sent) {
                response.put("success", true);
                response.put("message", "인증번호가 발송되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "인증번호 발송에 실패했습니다.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이메일 발송 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 인증번호 확인
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        Map<String, Object> response = new HashMap<>();
        
        if (email == null || code == null) {
            response.put("success", false);
            response.put("message", "이메일과 인증번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            boolean verified = emailService.verifyCode(email, code);
            
            if (verified) {
                response.put("success", true);
                response.put("message", "이메일 인증이 완료되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "인증번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "인증번호 확인 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
