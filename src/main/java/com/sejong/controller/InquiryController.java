package com.sejong.controller;

import com.sejong.entity.Inquiry;
import com.sejong.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    
    /**
     * 문의 게시판 목록 페이지
     */
    @GetMapping("/inquiry")
    public String inquiryList(Model model) {
        List<Inquiry> publicInquiries = inquiryService.getPublicInquiries();
        model.addAttribute("inquiries", publicInquiries);
        return "inquiry/list";
    }
    
    /**
     * 문의 작성 페이지
     */
    @GetMapping("/inquiry/write")
    public String inquiryWriteForm() {
        return "inquiry/write";
    }
    
    /**
     * 문의 등록
     */
    @PostMapping("/inquiry")
    @ResponseBody
    public String registerInquiry(@RequestParam String name,
                                 @RequestParam(required = false) String email,
                                 @RequestParam String message,
                                 @RequestParam(defaultValue = "true") Boolean isPublic) {
        try {
            // 입력값 검증
            if (name == null || name.trim().isEmpty()) {
                return "error: 이름을 입력해주세요.";
            }
            
            if (message == null || message.trim().isEmpty()) {
                return "error: 문의 내용을 입력해주세요.";
            }
            
            inquiryService.registerInquiry(name.trim(), email, message.trim(), isPublic);
            return "success";
        } catch (Exception e) {
            return "error: 문의 등록 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
    
    /**
     * 문의 상세 보기
     */
    @GetMapping("/inquiry/{inquiryId}")
    public String inquiryDetail(@PathVariable Integer inquiryId, Model model) {
        Inquiry inquiry = inquiryService.getInquiryById(inquiryId);
        if (inquiry == null) {
            return "redirect:/inquiry";
        }
        
        model.addAttribute("inquiry", inquiry);
        return "inquiry/detail";
    }
}
