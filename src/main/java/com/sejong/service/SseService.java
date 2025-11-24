package com.sejong.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {
    
    // 사용자용 SSE 연결 관리 (단일 연결)
    private SseEmitter userEmitter;
    
    // 관리자용 SSE 연결 관리 (여러 관리자 지원)
    private final Map<Integer, SseEmitter> adminEmitters = new ConcurrentHashMap<>();
    
    // 새로운 족보 요청 카운터
    private int newJokboRequestCount = 0;
    
    /**
     * 사용자용 SSE Emitter 생성
     */
    public SseEmitter createUserEmitter() {
        // 기존 연결이 있으면 제거
        if (userEmitter != null) {
            try {
                userEmitter.complete();
            } catch (Exception e) {
                log.warn("기존 사용자 SSE 연결 해제 중 오류: {}", e.getMessage());
            }
        }
        
        // 새로운 연결 생성 (30분 타임아웃)
        userEmitter = new SseEmitter(30 * 60 * 1000L);
        
        // 연결 완료 시 정리
        userEmitter.onCompletion(() -> {
            log.info("사용자 SSE 연결 완료");
            userEmitter = null;
        });
        
        // 타임아웃 시 정리
        userEmitter.onTimeout(() -> {
            log.info("사용자 SSE 연결 타임아웃");
            userEmitter = null;
        });
        
        // 오류 시 정리
        userEmitter.onError((ex) -> {
            log.error("사용자 SSE 연결 오류: {}", ex.getMessage());
            userEmitter = null;
        });
        
        // 초기 연결 메시지 전송
        try {
            userEmitter.send(SseEmitter.event()
                .name("connect")
                .data("사용자 SSE 연결이 설정되었습니다."));
        } catch (IOException e) {
            log.error("사용자 SSE 초기 메시지 전송 실패: {}", e.getMessage());
        }
        
        log.info("사용자 SSE 연결 생성");
        return userEmitter;
    }
    
    /**
     * 관리자용 SSE Emitter 생성
     */
    public SseEmitter createAdminEmitter(Integer adminId) {
        // 기존 연결이 있으면 제거
        SseEmitter existingEmitter = adminEmitters.get(adminId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.warn("기존 관리자 SSE 연결 해제 중 오류: {}", e.getMessage());
            }
        }
        
        // 새로운 연결 생성 (30분 타임아웃)
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        
        // 연결 완료 시 정리
        emitter.onCompletion(() -> {
            log.info("관리자 {} SSE 연결 완료", adminId);
            adminEmitters.remove(adminId);
        });
        
        // 타임아웃 시 정리
        emitter.onTimeout(() -> {
            log.info("관리자 {} SSE 연결 타임아웃", adminId);
            adminEmitters.remove(adminId);
        });
        
        // 오류 시 정리
        emitter.onError((ex) -> {
            log.error("관리자 {} SSE 연결 오류: {}", adminId, ex.getMessage());
            adminEmitters.remove(adminId);
        });
        
        // 연결 저장
        adminEmitters.put(adminId, emitter);
        
        // 초기 연결 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                .name("connect")
                .data("관리자 SSE 연결이 설정되었습니다."));
        } catch (IOException e) {
            log.error("관리자 SSE 초기 메시지 전송 실패: {}", e.getMessage());
        }
        
        log.info("관리자 {} SSE 연결 생성", adminId);
        return emitter;
    }
    
    /**
     * 족보 승인 알림을 모든 사용자에게 전송
     */
    public void sendJokboApprovalNotification(String bookTitle, String jokboTitle) {
        if (userEmitter != null) {
            try {
                String message = String.format("족보가 승인되었습니다: %s - %s", bookTitle, jokboTitle);
                userEmitter.send(SseEmitter.event()
                    .name("jokbo_approved")
                    .data(message));
                log.info("족보 승인 알림 전송: {}", message);
            } catch (IOException e) {
                log.error("족보 승인 알림 전송 실패: {}", e.getMessage());
                userEmitter = null;
            }
        } else {
            log.warn("사용자 SSE 연결이 없어서 족보 승인 알림을 전송할 수 없습니다.");
        }
    }
    
    /**
     * 새로운 족보 요청 알림을 모든 관리자에게 전송
     */
    public void sendNewJokboRequestNotification(String bookTitle, String uploaderName) {
        newJokboRequestCount++;
        String message = String.format("새로운 족보 요청: %s (업로더: %s)", bookTitle, uploaderName);
        
        // 모든 관리자에게 알림 전송
        adminEmitters.entrySet().removeIf(entry -> {
            try {
                entry.getValue().send(SseEmitter.event()
                    .name("new_jokbo_request")
                    .data(message));
                return false;
            } catch (IOException e) {
                log.error("관리자 {} 알림 전송 실패: {}", entry.getKey(), e.getMessage());
                return true; // 연결 제거
            }
        });
        
        log.info("새로운 족보 요청 알림 전송: {}", message);
    }
    
    /**
     * 관리자 동기화 알림 전송
     */
    public void sendAdminSyncNotification(Integer adminId) {
        SseEmitter emitter = adminEmitters.get(adminId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("sync")
                    .data("동기화가 완료되었습니다."));
                log.info("관리자 {} 동기화 알림 전송", adminId);
            } catch (IOException e) {
                log.error("관리자 {} 동기화 알림 전송 실패: {}", adminId, e.getMessage());
                adminEmitters.remove(adminId);
            }
        }
    }
    
    /**
     * 사용자 Emitter 제거
     */
    public void removeUserEmitter() {
        if (userEmitter != null) {
            try {
                userEmitter.complete();
            } catch (Exception e) {
                log.warn("사용자 SSE 연결 해제 중 오류: {}", e.getMessage());
            }
            userEmitter = null;
        }
    }
    
    /**
     * 관리자 Emitter 제거
     */
    public void removeAdminEmitter(Integer adminId) {
        SseEmitter emitter = adminEmitters.remove(adminId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("관리자 {} SSE 연결 해제 중 오류: {}", adminId, e.getMessage());
            }
        }
    }
    
    /**
     * 새로운 족보 요청 카운트 가져오기
     */
    public int getNewJokboRequestCount() {
        return newJokboRequestCount;
    }
    
    /**
     * 새로운 족보 요청 카운트 리셋
     */
    public void resetNewJokboRequestCount() {
        newJokboRequestCount = 0;
    }
}
