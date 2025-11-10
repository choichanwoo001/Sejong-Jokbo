package com.sejong.controller.api;


import com.sejong.entity.JokboApprovalHistory;
import com.sejong.global.dto.ApiResponse;
import com.sejong.service.JokboApprovalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/jokbo-approval-history")
@RequiredArgsConstructor
@Tag(name = "Jokbo Approval History API", description = "족보 승인 이력 관련 API")
public class JokboApprovalHistoryRestController {
    
    private final JokboApprovalHistoryService historyService;
    
    @Operation(summary = "족보별 승인 이력 조회", description = "특정 족보의 승인 이력을 조회합니다")
    @GetMapping("/jokbo/{jokboId}")
    public ResponseEntity<ApiResponse<List<JokboApprovalHistory>>> getHistoryByJokbo(
            @Parameter(description = "족보 ID") @PathVariable Integer jokboId) {
        
        List<JokboApprovalHistory> history = historyService.getHistoryByJokboId(jokboId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
    
    @Operation(summary = "관리자별 승인 이력 조회", description = "특정 관리자의 승인 처리 이력을 조회합니다")
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<ApiResponse<Page<JokboApprovalHistory>>> getHistoryByAdmin(
            @Parameter(description = "관리자 ID") @PathVariable Integer adminId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            HttpSession session) {
        
        Integer sessionAdminId = (Integer) session.getAttribute("adminId");
        if (sessionAdminId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("관리자 로그인이 필요합니다.", "UNAUTHORIZED"));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JokboApprovalHistory> history = historyService.getHistoryByAdminId(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
    
    @Operation(summary = "전체 승인 이력 조회", description = "모든 족보의 승인 이력을 조회합니다 (관리자 전용)")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<JokboApprovalHistory>>> getAllHistory(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "액션 필터") @RequestParam(required = false) JokboApprovalHistory.ApprovalAction action,
            HttpSession session) {
        
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("관리자 로그인이 필요합니다.", "UNAUTHORIZED"));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JokboApprovalHistory> history;
        
        if (action != null) {
            history = historyService.getHistoryByAction(action, pageable);
        } else {
            history = historyService.getAllHistory(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(history));
    }
    
    @Operation(summary = "내 승인 이력 조회", description = "현재 로그인한 관리자의 승인 처리 이력을 조회합니다")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<JokboApprovalHistory>>> getMyHistory(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            HttpSession session) {
        
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("관리자 로그인이 필요합니다.", "UNAUTHORIZED"));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JokboApprovalHistory> history = historyService.getHistoryByAdminId(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}