package com.sejong.service;

import com.sejong.entity.Book;
import com.sejong.repository.BookRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    
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
    
    /**
     * ID로 도서를 조회합니다.
     * @param bookId 도서 ID
     * @return 해당 ID의 도서
     */
    public Book getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));
    }
} 