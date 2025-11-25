package com.sejong.repository;

import com.sejong.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {

    /**
     * 모든 문의를 페이징하여 최신순으로 조회합니다 (댓글 포함)
     */
    @Query("SELECT DISTINCT i FROM Inquiry i LEFT JOIN FETCH i.comments ORDER BY i.createdAt DESC")
    Page<Inquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 답변되지 않은 문의만 페이징하여 조회합니다 (댓글 포함)
     */
    @Query("SELECT DISTINCT i FROM Inquiry i LEFT JOIN FETCH i.comments WHERE i.comments IS EMPTY ORDER BY i.createdAt DESC")
    Page<Inquiry> findByCommentsIsEmptyOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 모든 문의를 최신순으로 조회합니다 (페이징 없음, 댓글 포함)
     */
    @Query("SELECT DISTINCT i FROM Inquiry i LEFT JOIN FETCH i.comments ORDER BY i.createdAt DESC")
    List<Inquiry> findAllByOrderByCreatedAtDesc();

    /**
     * 답변되지 않은 문의만 조회합니다 (페이징 없음, 댓글 포함)
     */
    @Query("SELECT DISTINCT i FROM Inquiry i LEFT JOIN FETCH i.comments WHERE i.comments IS EMPTY ORDER BY i.createdAt DESC")
    List<Inquiry> findByCommentsIsEmptyOrderByCreatedAtDesc();
}
