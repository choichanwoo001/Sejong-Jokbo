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
     * 모든 문의를 페이징하여 조회합니다 (15개씩)
     */
    public Page<Inquiry> getAllInquiries(int page) {
        Pageable pageable = PageRequest.of(page, 15); // 15개씩 페이징
        return inquiryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 모든 문의를 페이징하여 조회합니다 (커스텀 페이지 크기)
     */
    public Page<Inquiry> getAllInquiries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inquiryRepository.findAllByOrderByCreatedAtDesc(pageable);
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
     * 답변되지 않은 문의만 조회합니다 (페이징 없음)
     */
    public List<Inquiry> getUnansweredInquiries() {
        return inquiryRepository.findByCommentsIsEmptyOrderByCreatedAtDesc();
    }

    /**
     * 문의 ID로 문의를 조회합니다
     */
    public Inquiry getInquiryById(@org.springframework.lang.NonNull Integer inquiryId) {
        return inquiryRepository.findById(inquiryId).orElse(null);
    }

    /**
     * 문의 ID로 문의를 조회합니다 (댓글 포함)
     */
    public Inquiry getInquiryWithComments(@org.springframework.lang.NonNull Integer inquiryId) {
        return inquiryRepository.findByIdWithComments(inquiryId).orElse(null);
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
    public Comment addComment(@org.springframework.lang.NonNull Integer inquiryId, String content) {
        Inquiry inquiry = getInquiryById(inquiryId);
        Admin admin = adminService.getOrCreateDefaultAdmin();

        if (inquiry == null) {
            throw new IllegalArgumentException("문의를 찾을 수 없습니다.");
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
    public List<Comment> getCommentsByInquiryId(@org.springframework.lang.NonNull Integer inquiryId) {
        return commentRepository.findByInquiryInquiryIdOrderByCreatedAtAsc(inquiryId);
    }

    /**
     * 문의를 삭제합니다 (답변도 함께 삭제됨)
     */
    public void deleteInquiry(@org.springframework.lang.NonNull Integer inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);
        if (inquiry == null) {
            throw new IllegalArgumentException("문의를 찾을 수 없습니다.");
        }

        // 연관된 답변 삭제 (Cascade 설정이 되어있다면 필요없을 수 있지만 명시적으로 처리)
        List<Comment> comments = commentRepository.findByInquiryInquiryIdOrderByCreatedAtAsc(inquiryId);
        commentRepository.deleteAll(comments);

        inquiryRepository.delete(inquiry);
    }
}
