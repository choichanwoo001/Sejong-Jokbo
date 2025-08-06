package com.sejong.controller;

import com.sejong.entity.Book;
import com.sejong.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import java.util.List;

/**
 * 페이지 컨트롤러
 * 홈 페이지 및 기타 정적 페이지들을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    private final BookService bookService;

    /**
     * 홈 페이지를 반환합니다.
     * @param model 뷰에 전달할 데이터 모델
     * @return 홈 페이지 뷰
     */
    @GetMapping("/")
    public String home(Model model) {
        // 각 카테고리별 도서 목록을 가져옵니다
        List<Book> westernBooks = bookService.getBooksByCategory("서양");
        List<Book> eastWestBooks = bookService.getBooksByCategory("동서양");
        List<Book> easternBooks = bookService.getBooksByCategory("동양");
        List<Book> scienceBooks = bookService.getBooksByCategory("과학");
        
        // 모델에 데이터를 추가합니다
        model.addAttribute("westernBooks", westernBooks);
        model.addAttribute("eastWestBooks", eastWestBooks);
        model.addAttribute("easternBooks", easternBooks);
        model.addAttribute("scienceBooks", scienceBooks);
        
        return "home";
    }
} 