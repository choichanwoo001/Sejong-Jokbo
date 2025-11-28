package com.sejong.controller.api;

import com.sejong.service.AdminService;
import com.sejong.service.InquiryService;
import com.sejong.service.JokboService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 API", description = "관리자 기능 관련 API")
public class AdminRestController {

    private final AdminService adminService;
    private final InquiryService inquiryService;
    private final JokboService jokboService;

    /**
     * 문의에 답변 추가
     */
    @Operation(summary = "문의 답변 추가", description = "문의에 관리자 답변을 추가합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답변 추가 성공/실패 메시지")
    })
    @PostMapping("/inquiry/{inquiryId}/comment")
    public String addComment(
            @Parameter(description = "문의 ID") @PathVariable @org.springframework.lang.NonNull Integer inquiryId,
            @Parameter(description = "답변 내용") @RequestParam String content) {
        try {
            inquiryService.addComment(inquiryId, content);
            return "success";
        } catch (Exception e) {
            return "error: 답변 등록 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }

    /**
     * 족보 승인
     */
    @Operation(summary = "족보 승인", description = "대기 중인 족보를 승인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공/실패 메시지")
    })
    @PostMapping("/jokbo/{jokboId}/approve")
    public String approveJokbo(
            @Parameter(description = "족보 ID") @PathVariable @org.springframework.lang.NonNull Integer jokboId,
            @Parameter(description = "승인 코멘트") @RequestParam(required = false) String comment) {
        try {
            adminService.approveJokbo(jokboId, comment);
            return "success";
        } catch (Exception e) {
            return "error: 족보 승인 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }

    /**
     * 족보 반려
     */
    @Operation(summary = "족보 반려", description = "대기 중인 족보를 반려합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "반려 성공/실패 메시지")
    })
    @PostMapping("/jokbo/{jokboId}/reject")
    public String rejectJokbo(
            @Parameter(description = "족보 ID") @PathVariable @org.springframework.lang.NonNull Integer jokboId,
            @Parameter(description = "반려 코멘트") @RequestParam(required = false) String comment) {
        try {
            adminService.rejectJokbo(jokboId, comment);
            return "success";
        } catch (Exception e) {
            return "error: 족보 반려 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }

    /**
     * 족보 승인 취소
     */
    @Operation(summary = "족보 승인 취소", description = "승인된 족보를 취소합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 취소 성공/실패 메시지")
    })
    @PostMapping("/jokbo/{jokboId}/cancel-approval")
    public String cancelApproval(
            @Parameter(description = "족보 ID") @PathVariable @org.springframework.lang.NonNull Integer jokboId,
            @Parameter(description = "취소 코멘트") @RequestParam(required = false) String comment) {
        try {
            adminService.cancelApproval(jokboId, comment);
            return "success";
        } catch (Exception e) {
            return "error: 족보 승인 취소 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }

    /**
     * 승인 대기 중인 족보 수를 반환합니다
     */
    @Operation(summary = "승인 대기 족보 수", description = "승인 대기 중인 족보의 개수를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "족보 수 반환 성공")
    })
    @GetMapping("/jokbos/pending/count")
    public java.util.Map<String, Object> getPendingJokbosCount() {
        long count = jokboService.getPendingJokbosCount();
        return java.util.Map.of("count", count);
    }
}
