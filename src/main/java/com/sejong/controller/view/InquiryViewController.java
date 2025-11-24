package com.sejong.controller.view;

import com.sejong.entity.Inquiry;
import com.sejong.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequiredArgsConstructor
@Tag(name = "문의 뷰", description = "문의 게시판 페이지 관련")
public class InquiryViewController {

    private final InquiryService inquiryService;
    
    /**
     * 문의 게시판 목록 페이지 (페이징 포함)
     */
    @Operation(summary = "문의 목록 조회", description = "공개된 문의 목록을 페이징하여 조회합니다")
    @GetMapping("/inquiry")
    public String inquiryList(@Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page, Model model) {
        Page<Inquiry> inquiryPage = inquiryService.getPublicInquiries(page);
        
        model.addAttribute("inquiries", inquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiryPage.getTotalPages());
        model.addAttribute("hasNext", inquiryPage.hasNext());
        model.addAttribute("hasPrevious", inquiryPage.hasPrevious());
        
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
