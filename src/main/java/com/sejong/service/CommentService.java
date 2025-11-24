package com.sejong.service;

import com.sejong.entity.Comment;
import com.sejong.entity.Inquiry;
import com.sejong.repository.CommentRepository;
import com.sejong.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final InquiryRepository inquiryRepository;
    private final AdminService adminService;
    
    /**
     * 문의에 대한 모든 답변 조회
     */
    public List<Comment> getCommentsByInquiryId(Integer inquiryId) {
        return commentRepository.findByInquiryInquiryIdOrderByCreatedAtAsc(inquiryId);
    }
    
    /**
     * 관리자가 문의에 답변 작성
     */
    @Transactional
    public Comment createComment(Integer inquiryId, String content) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("문의를 찾을 수 없습니다."));
        
        Comment comment = new Comment();
        comment.setInquiry(inquiry);
        comment.setAdmin(adminService.getOrCreateDefaultAdmin());
        comment.setContent(content);
        
        return commentRepository.save(comment);
    }
    
    /**
     * 답변 수정
     */
    @Transactional
    public Comment updateComment(Integer commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("답변을 찾을 수 없습니다."));
        
        // 작성자 본인만 수정 가능
        Integer defaultAdminId = adminService.getOrCreateDefaultAdmin().getAdminId();
        if (!comment.getAdmin().getAdminId().equals(defaultAdminId)) {
            throw new IllegalArgumentException("본인이 작성한 답변만 수정할 수 있습니다.");
        }
        
        comment.setContent(content);
        return commentRepository.save(comment);
    }
    
    /**
     * 답변 삭제
     */
    @Transactional
    public void deleteComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("답변을 찾을 수 없습니다."));
        
        // 작성자 본인만 삭제 가능
        Integer defaultAdminId = adminService.getOrCreateDefaultAdmin().getAdminId();
        if (!comment.getAdmin().getAdminId().equals(defaultAdminId)) {
            throw new IllegalArgumentException("본인이 작성한 답변만 삭제할 수 있습니다.");
        }
        
        commentRepository.delete(comment);
    }
}