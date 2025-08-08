package com.sejong.service;

import com.sejong.entity.Admin;
import com.sejong.entity.Comment;
import com.sejong.entity.Inquiry;
import com.sejong.repository.CommentRepository;
import com.sejong.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    
    private final InquiryRepository inquiryRepository;
    private final CommentRepository commentRepository;
    private final AdminService adminService;
    
    /**
     * 모든 문의를 페이징하여 조회합니다 (10개씩)
     */
    public Page<Inquiry> getAllInquiries(int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징
        return inquiryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * 공개된 문의만 페이징하여 조회합니다 (10개씩)
     */
    public Page<Inquiry> getPublicInquiries(int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징
        return inquiryRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * 답변되지 않은 문의만 페이징하여 조회합니다 (10개씩)
     */
    public Page<Inquiry> getUnansweredInquiries(int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징
        return inquiryRepository.findByCommentsIsEmptyOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * 모든 문의를 조회합니다 (페이징 없음)
     */
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * 공개된 문의만 조회합니다 (페이징 없음)
     */
    public List<Inquiry> getPublicInquiries() {
        return inquiryRepository.findByIsPublicTrueOrderByCreatedAtDesc();
    }
    
    /**
     * 답변되지 않은 문의만 조회합니다 (페이징 없음)
     */
    public List<Inquiry> getUnansweredInquiries() {
        return inquiryRepository.findByCommentsIsEmptyOrderByCreatedAtDesc();
    }
    
    /**
     * 문의 ID로 문의를 조회합니다
     */
    public Inquiry getInquiryById(Integer inquiryId) {
        return inquiryRepository.findById(inquiryId).orElse(null);
    }
    
    /**
     * 새로운 문의를 등록합니다
     */
    public Inquiry registerInquiry(String name, String email, String message, Boolean isPublic) {
        Inquiry inquiry = new Inquiry();
        inquiry.setName(name);
        inquiry.setEmail(email);
        inquiry.setMessage(message);
        inquiry.setIsPublic(isPublic);
        
        return inquiryRepository.save(inquiry);
    }
    
    /**
     * 문의에 답변을 추가합니다
     */
    public Comment addComment(Integer inquiryId, Integer adminId, String content) {
        Inquiry inquiry = getInquiryById(inquiryId);
        Admin admin = adminService.getAdminById(adminId);
        
        if (inquiry == null || admin == null) {
            throw new IllegalArgumentException("문의 또는 관리자를 찾을 수 없습니다.");
        }
        
        Comment comment = new Comment();
        comment.setInquiry(inquiry);
        comment.setAdmin(admin);
        comment.setContent(content);
        
        return commentRepository.save(comment);
    }
    
    /**
     * 문의 ID로 답변들을 조회합니다
     */
    public List<Comment> getCommentsByInquiryId(Integer inquiryId) {
        return commentRepository.findByInquiryInquiryIdOrderByCreatedAtAsc(inquiryId);
    }
}
