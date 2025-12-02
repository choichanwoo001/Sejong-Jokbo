package com.sejong.repository;

import com.sejong.entity.Jokbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JokboRepository extends JpaRepository<Jokbo, Integer> {

    /**
     * 특정 책의 승인된 족보 목록을 페이징하여 가져옵니다
     */
    @Query("SELECT j FROM Jokbo j WHERE j.book.bookId = :bookId AND j.status = '승인' ORDER BY j.createdAt DESC")
    Page<Jokbo> findApprovedJokbosByBookId(@Param("bookId") Integer bookId, Pageable pageable);

    /**
     * 특정 책의 모든 족보 목록을 페이징하여 가져옵니다 (관리자용)
     */
    @Query("SELECT j FROM Jokbo j WHERE j.book.bookId = :bookId ORDER BY j.createdAt DESC")
    Page<Jokbo> findAllJokbosByBookId(@Param("bookId") Integer bookId, Pageable pageable);

    /**
     * 특정 책의 승인된 족보 목록을 가져옵니다 (페이징 없음)
     */
    @Query("SELECT j FROM Jokbo j WHERE j.book.bookId = :bookId AND j.status = '승인' ORDER BY j.createdAt DESC")
    List<Jokbo> findApprovedJokbosByBookId(@Param("bookId") Integer bookId);

    /**
     * 특정 책의 모든 족보 목록을 가져옵니다 (관리자용, 페이징 없음)
     */
    @Query("SELECT j FROM Jokbo j WHERE j.book.bookId = :bookId ORDER BY j.createdAt DESC")
    List<Jokbo> findAllJokbosByBookId(@Param("bookId") Integer bookId);

    /**
     * 특정 상태의 족보 수를 가져옵니다
     */
    @Query("SELECT COUNT(j) FROM Jokbo j WHERE j.status = :status")
    long countByStatus(@Param("status") Jokbo.JokboStatus status);

    /**
     * 특정 상태의 족보 목록을 페이징하여 최신순으로 가져옵니다 (Book 정보 포함)
     */
    @Query("SELECT j FROM Jokbo j JOIN FETCH j.book WHERE j.status = :status ORDER BY j.createdAt DESC")
    Page<Jokbo> findByStatusWithBookOrderByCreatedAtDesc(@Param("status") Jokbo.JokboStatus status, Pageable pageable);

    /**
     * 특정 상태의 족보 목록을 페이징하여 최신순으로 가져옵니다 (기본 쿼리)
     */
    @Query("SELECT j FROM Jokbo j WHERE j.status = :status ORDER BY j.createdAt DESC")
    Page<Jokbo> findByStatusOrderByCreatedAtDesc(@Param("status") Jokbo.JokboStatus status, Pageable pageable);

    /**
     * 특정 상태의 족보 목록을 최신순으로 가져옵니다 (페이징 없음, Book 정보 포함)
     */
    @Query("SELECT j FROM Jokbo j JOIN FETCH j.book WHERE j.status = :status ORDER BY j.createdAt DESC")
    List<Jokbo> findByStatusOrderByCreatedAtDesc(@Param("status") Jokbo.JokboStatus status);

    /**
     * 모든 족보 목록을 최신순으로 가져옵니다 (Book 정보 포함, 페이징)
     */
    @Query("SELECT j FROM Jokbo j JOIN FETCH j.book ORDER BY j.createdAt DESC")
    Page<Jokbo> findAllWithBookOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 모든 족보 목록을 최신순으로 페이징하여 가져옵니다 (기본 쿼리)
     */
    @Query("SELECT j FROM Jokbo j ORDER BY j.createdAt DESC")
    Page<Jokbo> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 특정 책의 특정 상태 족보 수를 가져옵니다
     */
    @Query("SELECT COUNT(j) FROM Jokbo j WHERE j.book.bookId = :bookId AND j.status = :status")
    long countByBookIdAndStatus(@Param("bookId") Integer bookId, @Param("status") Jokbo.JokboStatus status);

    /**
     * contentUrl로 족보를 찾습니다
     */
    Optional<Jokbo> findByContentUrl(String contentUrl);

    /**
     * 전체 다운로드 수를 합산합니다
     */
    @Query("SELECT SUM(j.downloadCount) FROM Jokbo j")
    Long sumDownloadCount();
}