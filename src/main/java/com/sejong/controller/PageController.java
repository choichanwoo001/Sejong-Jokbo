package com.sejong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 페이지 컨트롤러
 * 홈 페이지 및 기타 정적 페이지들을 처리합니다.
 */
@Controller
public class PageController {

    /**
     * 홈 페이지를 반환합니다.
     * @return 홈 페이지 뷰
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
} 