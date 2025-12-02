package com.sejong.service;

import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.repository.BookRepository;
import com.sejong.repository.JokboRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final JokboRepository jokboRepository;

    /**
     * 모든 도서를 조회합니다.
     * 
     * @return 전체 도서 목록
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * 카테고리별 도서를 조회합니다.
     * 
     * @param category 카테고리
     * @return 해당 카테고리의 도서 목록
     */
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategoryOrderByTitleAsc(category);
    }

    /**
     * 제목으로 도서를 검색합니다.
     * 
     * @param title 검색할 제목
     * @return 검색 결과 도서 목록
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    /**
     * 저자로 도서를 검색합니다.
     * 
     * @param author 검색할 저자
     * @return 검색 결과 도서 목록
     */
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    /**
     * 통합 검색을 수행합니다.
     * 
     * @param keyword 검색 키워드
     * @return 검색 결과 도서 목록
     */
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchBooks(keyword.trim());
    }

    /**
     * 카테고리와 키워드로 검색합니다.
     * 
     * @param category 카테고리
     * @param keyword  검색 키워드
     * @return 검색 결과 도서 목록
     */
    public List<Book> searchBooksByCategoryAndKeyword(String category, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getBooksByCategory(category);
        }
        return bookRepository.searchBooksByCategoryAndKeyword(category, keyword.trim());
    }

    /**
     * ID로 도서를 조회합니다.
     * 
     * @param bookId 도서 ID
     * @return 해당 ID의 도서
     */
    public Book getBookById(@org.springframework.lang.NonNull Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));
    }

    /**
     * 모든 책의 jokboCount를 실시간으로 업데이트합니다 (초기화용)
     */
    public void updateAllJokboCounts() {
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {
            long approvedCount = jokboRepository.countByBookIdAndStatus(book.getBookId(), Jokbo.JokboStatus.승인);
            book.setJokboCount((int) approvedCount);
            bookRepository.save(book);
        }
    }

    /**
     * 특정 책의 jokboCount를 업데이트합니다
     */
    public void updateJokboCount(@org.springframework.lang.NonNull Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다"));

        long approvedCount = jokboRepository.countByBookIdAndStatus(bookId, Jokbo.JokboStatus.승인);
        book.setJokboCount((int) approvedCount);
        bookRepository.save(book);
    }
}