package com.sejong.repository;

import com.sejong.entity.JokboApprovalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JokboApprovalHistoryRepository extends JpaRepository<JokboApprovalHistory, Integer> {
    
    /**
     * 특정 족보의 승인 이력 조회 (최신순)
     */
    List<JokboApprovalHistory> findByJokboJokboIdOrderByCreatedAtDesc(Integer jokboId);
    
    /**
     * 특정 관리자의 승인 처리 이력 조회 (최신순)
     */
    Page<JokboApprovalHistory> findByAdminAdminIdOrderByCreatedAtDesc(Integer adminId, Pageable pageable);
    
    /**
     * 모든 승인 이력 조회 (최신순, 페이징)
     */
    Page<JokboApprovalHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 특정 액션 타입별 이력 조회
     */
    List<JokboApprovalHistory> findByActionOrderByCreatedAtDesc(JokboApprovalHistory.ApprovalAction action);
    
    /**
     * 족보별 승인 이력 통계 (승인/반려 횟수)
     */
    @Query("SELECT h.action, COUNT(h) FROM JokboApprovalHistory h WHERE h.jokbo.jokboId = :jokboId GROUP BY h.action")
    List<Object[]> countApprovalActionsByJokboId(@Param("jokboId") Integer jokboId);
    
    /**
     * 관리자별 승인 처리 통계
     */
    @Query("SELECT h.action, COUNT(h) FROM JokboApprovalHistory h WHERE h.admin.adminId = :adminId GROUP BY h.action")
    List<Object[]> countApprovalActionsByAdminId(@Param("adminId") Integer adminId);
}
