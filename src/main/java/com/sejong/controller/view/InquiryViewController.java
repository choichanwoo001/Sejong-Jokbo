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
    private final com.sejong.service.JokboApprovalHistoryService historyService;

    /**
     * 문의 게시판 목록 페이지 (페이징 포함)
     */
    @Operation(summary = "문의 목록 조회", description = "공개된 문의 목록을 페이징하여 조회합니다")
    @GetMapping("/inquiry")
    public String inquiryList(@Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Inquiry> inquiryPage = inquiryService.getAllInquiries(page);
        model.addAttribute("inquiries", inquiryPage.getContent());
        model.addAttribute("totalPages", inquiryPage.getTotalPages());
        model.addAttribute("hasNext", inquiryPage.hasNext());
        model.addAttribute("hasPrevious", inquiryPage.hasPrevious());
        model.addAttribute("currentPage", page);

        return "inquiry/list";
    }

    /**
     * 족보 승인 이력 페이지
     */
    @Operation(summary = "승인 이력 조회", description = "모든 족보 승인 이력을 조회합니다")
    @GetMapping("/approval/history")
    public String approvalHistoryList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<com.sejong.entity.JokboApprovalHistory> historyPage = historyService
                .getPublicApprovalHistory(org.springframework.data.domain.PageRequest.of(page, 15));

        model.addAttribute("approvalHistories", historyPage.getContent());
        model.addAttribute("totalPages", historyPage.getTotalPages());
        model.addAttribute("hasNext", historyPage.hasNext());
        model.addAttribute("hasPrevious", historyPage.hasPrevious());
        model.addAttribute("currentPage", page);

        return "approval/history";
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
    public String inquiryDetail(@PathVariable @org.springframework.lang.NonNull Integer inquiryId, Model model) {
        Inquiry inquiry = inquiryService.getInquiryWithComments(inquiryId);
        if (inquiry == null) {
            return "redirect:/inquiry";
        }

        model.addAttribute("inquiry", inquiry);
        return "inquiry/detail";
    }
}
