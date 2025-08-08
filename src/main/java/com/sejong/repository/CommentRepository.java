package com.sejong.repository;

import com.sejong.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    /**
     * 문의 ID로 답변들을 조회합니다
     */
    List<Comment> findByInquiryInquiryIdOrderByCreatedAtAsc(Integer inquiryId);
}
