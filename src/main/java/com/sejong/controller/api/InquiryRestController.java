package com.sejong.controller.api;

import com.sejong.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@Tag(name = "문의 API", description = "문의 등록 관련 API")
public class InquiryRestController {

    private final InquiryService inquiryService;
    
    /**
     * 문의 등록
     */
    @Operation(summary = "문의 등록", description = "새로운 문의를 등록합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록 성공/실패 메시지")
    })
    @PostMapping("/inquiry")
    public String registerInquiry(@Parameter(description = "이름") @RequestParam String name,
                                 @Parameter(description = "이메일") @RequestParam(required = false) String email,
                                 @Parameter(description = "문의 내용") @RequestParam String message,
                                 @Parameter(description = "공개 여부") @RequestParam(defaultValue = "true") Boolean isPublic) {
        try {
            // 입력값 검증
            if (name == null || name.trim().isEmpty()) {
                return "error: 이름을 입력해주세요.";
            }
            
            if (message == null || message.trim().isEmpty()) {
                return "error: 문의 내용을 입력해주세요.";
            }
            
            inquiryService.registerInquiry(name.trim(), email, message.trim(), isPublic);
            return "success";
        } catch (Exception e) {
            return "error: 문의 등록 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
}
