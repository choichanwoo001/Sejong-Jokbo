package com.sejong.service;

import com.sejong.entity.Book;
import com.sejong.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    /**
     * 모든 도서를 조회합니다.
     * @return 전체 도서 목록
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * 카테고리별 도서를 조회합니다.
     * @param category 카테고리
     * @return 해당 카테고리의 도서 목록
     */
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    /**
     * 제목으로 도서를 검색합니다.
     * @param title 검색할 제목
     * @return 검색 결과 도서 목록
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }
    
    /**
     * 저자로 도서를 검색합니다.
     * @param author 검색할 저자
     * @return 검색 결과 도서 목록
     */
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }
} 