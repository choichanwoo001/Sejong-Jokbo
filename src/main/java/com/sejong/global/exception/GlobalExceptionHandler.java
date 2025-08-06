package com.sejong.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 글로벌 예외 처리를 위한 핸들러
 * 공통적인 예외만 중앙에서 처리
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "서버 오류가 발생했습니다.");
        response.put("message", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "잘못된 요청입니다.");
        response.put("message", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * NullPointerException 처리
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "필수 데이터가 누락되었습니다.");
        response.put("message", "요청한 데이터를 찾을 수 없습니다.");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * 뷰 페이지용 예외 처리 (ModelAndView 반환)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleExceptionForView(Exception e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "오류가 발생했습니다: " + e.getMessage());
        mav.addObject("statusCode", "500");
        
        return mav;
    }
}
