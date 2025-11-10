package com.sejong.controller.api;

import com.sejong.global.dto.ApiResponse;
import com.sejong.service.BookService;
import com.sejong.service.JokboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "통계 API", description = "시스템 통계 관련 API")
public class StatsRestController {
    
    private final BookService bookService;
    private final JokboService jokboService;
    
    /**
     * 메인 페이지 통계 정보
     */
    @Operation(summary = "메인 페이지 통계", description = "메인 페이지에 표시할 통계 정보를 반환합니다")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMainStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // TODO: 서비스에 통계 메서드들 구현 필요
        stats.put("totalBooks", (long) bookService.getAllBooks().size());
        stats.put("totalJokbos", 0L); // 임시값
        stats.put("totalDownloads", 0L); // 임시값
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    /**
     * 관리자 대시보드 통계 정보
     */
    @Operation(summary = "관리자 대시보드 통계", description = "관리자 대시보드에 표시할 통계 정보를 반환합니다")
    @GetMapping("/admin/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // TODO: 서비스에 통계 메서드들 구현 필요
        stats.put("pendingJokbos", jokboService.getPendingJokbosCount());
        stats.put("totalJokbos", 0L); // 임시값
        stats.put("totalBooks", (long) bookService.getAllBooks().size());
        stats.put("pendingInquiries", 0L); // 임시값
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}