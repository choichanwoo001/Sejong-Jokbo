package com.sejong.repository;

import com.sejong.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    
    /**
     * 모든 문의를 최신순으로 조회합니다
     */
    List<Inquiry> findAllByOrderByCreatedAtDesc();
    
    /**
     * 공개된 문의만 최신순으로 조회합니다
     */
    List<Inquiry> findByIsPublicTrueOrderByCreatedAtDesc();
    
    /**
     * 답변되지 않은 문의만 조회합니다
     */
    List<Inquiry> findByCommentsIsEmptyOrderByCreatedAtDesc();
}
