package com.sejong.controller.api;

import com.sejong.entity.JokboApprovalHistory;
import com.sejong.global.dto.ApiResponse;
import com.sejong.service.AdminService;
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

import java.util.List;

@RestController
@RequestMapping("/api/jokbo-approval-history")
@RequiredArgsConstructor
@Tag(name = "Jokbo Approval History API", description = "족보 승인 이력 관련 API")
public class JokboApprovalHistoryRestController {
    private final JokboApprovalHistoryService historyService;
    private final AdminService adminService;

    @Operation(summary = "족보별 승인 이력 조회", description = "특정 족보의 승인 이력을 조회합니다")
    @GetMapping("/jokbo/{jokboId}")
    public ResponseEntity<ApiResponse<List<JokboApprovalHistory>>> getHistoryByJokbo(
            @Parameter(description = "족보 ID") @PathVariable @org.springframework.lang.NonNull Integer jokboId) {

        List<JokboApprovalHistory> history = historyService.getHistoryByJokboId(jokboId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @Operation(summary = "관리자별 승인 이력 조회", description = "특정 관리자의 승인 처리 이력을 조회합니다")
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<ApiResponse<Page<JokboApprovalHistory>>> getHistoryByAdmin(
            @Parameter(description = "관리자 ID") @PathVariable @org.springframework.lang.NonNull Integer adminId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JokboApprovalHistory> history = historyService.getHistoryByAdminId(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @Operation(summary = "전체 승인 이력 조회", description = "모든 족보의 승인 이력을 조회합니다 (관리자 전용)")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<JokboApprovalHistory>>> getAllHistory(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "액션 필터") @RequestParam(required = false) JokboApprovalHistory.ApprovalAction action) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JokboApprovalHistory> history;

        if (action != null) {
            history = historyService.getHistoryByAction(action, pageable);
        } else {
            history = historyService.getAllHistory(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @Operation(summary = "내 승인 이력 조회", description = "기본 관리자 계정의 승인 처리 이력을 조회합니다")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<JokboApprovalHistory>>> getMyHistory(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        Integer adminId = adminService.getOrCreateDefaultAdmin().getAdminId();
        Pageable pageable = PageRequest.of(page, size);
        Page<JokboApprovalHistory> history = historyService
                .getHistoryByAdminId(java.util.Objects.requireNonNull(adminId), pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}