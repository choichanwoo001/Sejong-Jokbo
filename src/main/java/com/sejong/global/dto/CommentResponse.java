package com.sejong.global.dto;

import com.sejong.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Integer commentId;
    private String content;
    private String adminName;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .adminName(comment.getAdmin().getAdminName())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
