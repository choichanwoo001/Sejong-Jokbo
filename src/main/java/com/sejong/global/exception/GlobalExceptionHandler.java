package com.sejong.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
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
     * 파일 크기 초과 예외 처리 (Spring Boot 레벨)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "파일 크기 초과");
        response.put("message", "업로드 가능한 파일 크기는 10MB까지입니다.");
        response.put("userAction", "파일 크기를 확인하고 10MB 이하의 파일을 선택해주세요.");

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(response);
    }

    /**
     * Multipart 요청 처리 예외
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> handleMultipartException(MultipartException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "파일 업로드 오류");
        response.put("message", "파일 업로드 중 문제가 발생했습니다.");
        response.put("userAction", "파일을 다시 선택하고 업로드를 시도해주세요.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 파일 I/O 예외 처리
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "파일 처리 오류");
        response.put("message", "파일 저장 중 오류가 발생했습니다.");
        response.put("userAction", "잠시 후 다시 시도해주세요. 문제가 지속되면 관리자에게 문의하세요.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * 비동기 요청 타임아웃 예외 처리 (SSE 등)
     * JSON 변환 에러 방지를 위해 body 없이 응답
     */
    @ExceptionHandler(org.springframework.web.context.request.async.AsyncRequestTimeoutException.class)
    public ResponseEntity<Void> handleAsyncRequestTimeoutException(
            org.springframework.web.context.request.async.AsyncRequestTimeoutException e) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }
}
