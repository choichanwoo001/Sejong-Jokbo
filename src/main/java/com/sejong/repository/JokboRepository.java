package com.sejong.repository;

import com.sejong.entity.Jokbo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JokboRepository extends JpaRepository<Jokbo, Integer> {
    
    /**
     * 특정 책의 승인된 족보 목록을 가져옵니다
     */
    @Query("SELECT j FROM Jokbo j WHERE j.book.bookId = :bookId AND j.status = '승인' ORDER BY j.createdAt DESC")
    List<Jokbo> findApprovedJokbosByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 특정 책의 모든 족보 목록을 가져옵니다 (관리자용)
     */
    @Query("SELECT j FROM Jokbo j WHERE j.book.bookId = :bookId ORDER BY j.createdAt DESC")
    List<Jokbo> findAllJokbosByBookId(@Param("bookId") Integer bookId);
} 