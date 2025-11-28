package com.sejong.controller.api;

import com.sejong.service.JokboService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "도서 API", description = "족보 등록 관련 API")
public class BookRestController {

    private final JokboService jokboService;

    /**
     * 텍스트 족보를 등록합니다
     */
    @Operation(summary = "텍스트 족보 등록", description = "텍스트 형태의 족보를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공/실패 메시지")
    })
    @PostMapping("/book/{bookId}/jokbo/text")
    public String registerTextJokbo(
            @Parameter(description = "도서 ID") @PathVariable @org.springframework.lang.NonNull Integer bookId,
            @Parameter(description = "업로더 이름") @RequestParam String uploaderName,
            @Parameter(description = "족보 내용") @RequestParam String content,
            @Parameter(description = "댓글") @RequestParam(required = false) String comment) {
        jokboService.registerTextJokbo(bookId, uploaderName, content, comment);
        return "success";
    }

    /**
     * 파일 족보를 등록합니다
     */
    @Operation(summary = "파일 족보 등록", description = "파일 형태의 족보를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공/실패 메시지")
    })
    @PostMapping("/book/{bookId}/jokbo/file")
    public String registerFileJokbo(
            @Parameter(description = "도서 ID") @PathVariable @org.springframework.lang.NonNull Integer bookId,
            @Parameter(description = "업로더 이름") @RequestParam String uploaderName,
            @Parameter(description = "업로드 파일") @RequestParam MultipartFile file,
            @Parameter(description = "댓글") @RequestParam(required = false) String comment) {
        try {
            // 입력값 검증
            if (uploaderName == null || uploaderName.trim().isEmpty()) {
                return "error: 업로더 이름을 입력해주세요.";
            }

            if (file == null || file.isEmpty()) {
                return "error: 업로드할 파일을 선택해주세요.";
            }

            jokboService.registerFileJokbo(bookId, uploaderName.trim(), file, comment);
            return "success";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        } catch (java.io.IOException e) {
            return "error: 파일 처리 중 오류가 발생했습니다. - " + e.getMessage();
        } catch (RuntimeException e) {
            return "error: " + e.getMessage();
        } catch (Exception e) {
            return "error: 족보 등록 중 예상치 못한 오류가 발생했습니다. - " + e.getMessage();
        }
    }
}
