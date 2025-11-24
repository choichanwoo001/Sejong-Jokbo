package com.sejong.controller.api;

import com.sejong.service.AdminService;
import com.sejong.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@Tag(name = "SSE API", description = "Server-Sent Events 관련 API")
public class SseController {
    
    private final SseService sseService;
    private final AdminService adminService;
    
    /**
     * 사용자용 SSE 연결 (족보 승인 알림)
     */
    @Operation(summary = "사용자 SSE 연결", description = "족보 승인 알림을 받기 위한 SSE 연결을 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SSE 연결 성공")
    })
    @GetMapping(value = "/user/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToUserNotifications() {
        return sseService.createUserEmitter();
    }
    
    /**
     * 관리자용 SSE 연결 (새로운 족보 요청 알림)
     */
    @Operation(summary = "관리자 SSE 연결", description = "새로운 족보 요청 알림을 받기 위한 SSE 연결을 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SSE 연결 성공")
    })
    @GetMapping(value = "/admin/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToAdminNotifications() {
        Integer adminId = adminService.getOrCreateDefaultAdmin().getAdminId();
        return sseService.createAdminEmitter(adminId);
    }
    
    /**
     * 관리자 동기화 요청 (새로운 족보 요청 확인)
     */
    @Operation(summary = "관리자 동기화", description = "새로운 족보 요청을 확인하고 알림을 보냅니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "동기화 성공")
    })
    @PostMapping("/admin/sync")
    public String syncAdminNotifications() {
        Integer adminId = adminService.getOrCreateDefaultAdmin().getAdminId();
        try {
            sseService.sendAdminSyncNotification(adminId);
            return "success";
        } catch (Exception e) {
            return "error: 동기화 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
    
    /**
     * SSE 연결 해제
     */
    @Operation(summary = "SSE 연결 해제", description = "SSE 연결을 해제합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "연결 해제 성공")
    })
    @DeleteMapping("/disconnect")
    public String disconnect(@Parameter(description = "연결 타입 (user/admin)") @RequestParam String type) {
        try {
            if ("admin".equals(type)) {
                Integer adminId = adminService.getOrCreateDefaultAdmin().getAdminId();
                sseService.removeAdminEmitter(adminId);
            } else {
                sseService.removeUserEmitter();
            }
            return "success";
        } catch (Exception e) {
            return "error: 연결 해제 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
}
