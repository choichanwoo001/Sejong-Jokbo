package com.sejong.controller.api;

import com.sejong.entity.Comment;
import com.sejong.global.dto.ApiResponse;
import com.sejong.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "문의 답변 관련 API")
public class CommentRestController {

    private final CommentService commentService;

    @Operation(summary = "문의 답변 목록 조회", description = "특정 문의에 대한 모든 답변을 조회합니다")
    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsByInquiry(
            @Parameter(description = "문의 ID") @PathVariable @org.springframework.lang.NonNull Integer inquiryId) {

        List<Comment> comments = commentService.getCommentsByInquiryId(inquiryId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @Operation(summary = "답변 작성", description = "관리자가 문의에 답변을 작성합니다")
    @PostMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @Parameter(description = "문의 ID") @PathVariable @org.springframework.lang.NonNull Integer inquiryId,
            @Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.createComment(inquiryId, request.getContent());
        return ResponseEntity.ok(ApiResponse.success("답변이 작성되었습니다.", comment));
    }

    @Operation(summary = "답변 수정", description = "작성한 답변을 수정합니다")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(
            @Parameter(description = "답변 ID") @PathVariable @org.springframework.lang.NonNull Integer commentId,
            @Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(ApiResponse.success("답변이 수정되었습니다.", comment));
    }

    @Operation(summary = "답변 삭제", description = "작성한 답변을 삭제합니다")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "답변 ID") @PathVariable @org.springframework.lang.NonNull Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("답변이 삭제되었습니다.", null));
    }

    // DTO 클래스
    public static class CommentRequest {
        @NotBlank(message = "답변 내용을 입력해주세요.")
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}