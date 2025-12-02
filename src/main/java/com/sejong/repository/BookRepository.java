package com.sejong.repository;

import com.sejong.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * 카테고리별 도서 목록을 조회합니다 (이름순 정렬).
     * 
     * @param category 카테고리
     * @return 해당 카테고리의 도서 목록
     */
    List<Book> findByCategoryOrderByTitleAsc(String category);

    /**
     * 카테고리별 도서 목록을 조회합니다.
     * 
     * @param category 카테고리
     * @return 해당 카테고리의 도서 목록
     */
    List<Book> findByCategory(String category);

    /**
     * 제목으로 도서를 검색합니다.
     * 
     * @param title 검색할 제목
     * @return 검색 결과 도서 목록
     */
    List<Book> findByTitleContaining(String title);

    /**
     * 저자로 도서를 검색합니다.
     * 
     * @param author 검색할 저자
     * @return 검색 결과 도서 목록
     */
    List<Book> findByAuthorContaining(String author);

    /**
     * 제목, 저자, 출판사로 통합 검색합니다.
     * 
     * @param keyword 검색 키워드
     * @return 검색 결과 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.publisher LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);

    /**
     * 카테고리와 키워드로 검색합니다.
     * 
     * @param category 카테고리
     * @param keyword  검색 키워드
     * @return 검색 결과 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.category = :category AND (b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.publisher LIKE %:keyword%)")
    List<Book> searchBooksByCategoryAndKeyword(@Param("category") String category, @Param("keyword") String keyword);
}