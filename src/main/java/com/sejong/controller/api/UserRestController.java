package com.sejong.controller.api;

import com.sejong.service.JokboService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 기능 관련 API")
public class UserRestController {

    private final JokboService jokboService;

    /**
     * 특정 책의 승인된 족보 목록을 가져옵니다
     */
    @Operation(summary = "족보 목록 조회", description = "특정 책의 승인된 족보 목록을 페이징하여 가져옵니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "족보 목록 조회 성공")
    })
    @GetMapping("/books/{bookId}/jokbos")
    public Map<String, Object> getApprovedJokbos(
            @Parameter(description = "책 ID") @PathVariable @org.springframework.lang.NonNull Integer bookId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page) {

        Page<?> jokboPage = jokboService.getApprovedJokbosByBookId(bookId, page);

        Map<String, Object> response = new HashMap<>();
        response.put("content", jokboPage.getContent());
        response.put("totalPages", jokboPage.getTotalPages());
        response.put("currentPage", jokboPage.getNumber());
        response.put("hasNext", jokboPage.hasNext());
        response.put("hasPrevious", jokboPage.hasPrevious());
        response.put("totalElements", jokboPage.getTotalElements());

        return response;
    }

    /**
     * 특정 책의 승인된 족보 개수를 가져옵니다
     */
    @Operation(summary = "족보 개수 조회", description = "특정 책의 승인된 족보 개수를 가져옵니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "족보 개수 조회 성공")
    })
    @GetMapping("/books/{bookId}/jokbos/count")
    public Map<String, Object> getApprovedJokbosCount(
            @Parameter(description = "책 ID") @PathVariable @org.springframework.lang.NonNull Integer bookId) {

        long count = jokboService.getApprovedJokbosByBookId(bookId).size();
        return Map.of("count", count);
    }
}
