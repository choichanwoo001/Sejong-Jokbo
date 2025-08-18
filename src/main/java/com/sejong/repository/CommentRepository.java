package com.sejong.repository;

import com.sejong.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    /**
     * 문의 ID로 답변들을 조회합니다 (관리자 정보 포함)
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.admin WHERE c.inquiry.inquiryId = :inquiryId ORDER BY c.createdAt ASC")
    List<Comment> findByInquiryInquiryIdOrderByCreatedAtAsc(@Param("inquiryId") Integer inquiryId);
}
