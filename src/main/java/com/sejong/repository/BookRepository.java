package com.sejong.repository;

import com.sejong.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    
    /**
     * 카테고리별 도서 목록을 조회합니다.
     * @param category 카테고리
     * @return 해당 카테고리의 도서 목록
     */
    List<Book> findByCategory(String category);
    
    /**
     * 제목으로 도서를 검색합니다.
     * @param title 검색할 제목
     * @return 검색 결과 도서 목록
     */
    List<Book> findByTitleContaining(String title);
    
    /**
     * 저자로 도서를 검색합니다.
     * @param author 검색할 저자
     * @return 검색 결과 도서 목록
     */
    List<Book> findByAuthorContaining(String author);
} 