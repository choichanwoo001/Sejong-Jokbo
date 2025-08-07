package com.sejong.controller;

import com.sejong.entity.Admin;
import com.sejong.entity.Book;
import com.sejong.entity.Comment;
import com.sejong.entity.Inquiry;
import com.sejong.entity.Jokbo;
import com.sejong.service.AdminService;
import com.sejong.service.BookService;
import com.sejong.service.InquiryService;
import com.sejong.service.JokboService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    private final BookService bookService;
    private final JokboService jokboService;
    private final InquiryService inquiryService;
    
    /**
     * 관리자 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }
    
    /**
     * 관리자 로그인 처리
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String adminName, 
                       @RequestParam String password,
                       HttpSession session) {
        Admin admin = adminService.login(adminName, password);
        if (admin != null) {
            session.setAttribute("adminId", admin.getAdminId());
            session.setAttribute("adminName", admin.getAdminName());
            return "success";
        } else {
            return "error: 로그인 정보가 올바르지 않습니다.";
        }
    }
    
    /**
     * 관리자 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
    
    /**
     * 관리자 대시보드
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        // 승인 대기 중인 족보 수
        long pendingJokbos = jokboService.getPendingJokbosCount();
        
        // 답변되지 않은 문의 수
        List<Inquiry> unansweredInquiries = inquiryService.getUnansweredInquiries();
        
        model.addAttribute("pendingJokbos", pendingJokbos);
        model.addAttribute("unansweredInquiries", unansweredInquiries);
        
        return "admin/dashboard";
    }
    
    /**
     * 모든 책 목록 (관리자용)
     */
    @GetMapping("/books")
    public String adminBooks(HttpSession session, Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        
        return "admin/books";
    }
    
    /**
     * 승인 대기 중인 족보 목록 (페이징 없음)
     */
    @GetMapping("/jokbos/pending")
    public String pendingJokbos(HttpSession session, Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        List<Jokbo> pendingJokbos = jokboService.getPendingJokbos();
        model.addAttribute("jokbos", pendingJokbos);
        
        return "admin/pending-jokbos";
    }
    
    /**
     * 승인 대기 중인 족보 목록 (페이징 포함)
     */
    @GetMapping("/jokbos/pending/page/{page}")
    public String pendingJokbosWithPaging(@PathVariable int page, 
                                         HttpSession session, 
                                         Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Page<Jokbo> jokboPage = jokboService.getPendingJokbos(page);
        
        model.addAttribute("jokbos", jokboPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jokboPage.getTotalPages());
        model.addAttribute("hasNext", jokboPage.hasNext());
        model.addAttribute("hasPrevious", jokboPage.hasPrevious());
        
        return "admin/pending-jokbos";
    }
    
    /**
     * 문의 목록 (페이징 없음)
     */
    @GetMapping("/inquiries")
    public String inquiries(HttpSession session, Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        model.addAttribute("inquiries", inquiries);
        
        return "admin/inquiries";
    }
    
    /**
     * 문의 목록 (페이징 포함)
     */
    @GetMapping("/inquiries/page/{page}")
    public String inquiriesWithPaging(@PathVariable int page, 
                                     HttpSession session, 
                                     Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Page<Inquiry> inquiryPage = inquiryService.getAllInquiries(page);
        
        model.addAttribute("inquiries", inquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiryPage.getTotalPages());
        model.addAttribute("hasNext", inquiryPage.hasNext());
        model.addAttribute("hasPrevious", inquiryPage.hasPrevious());
        
        return "admin/inquiries";
    }
    
    /**
     * 문의 상세 페이지
     */
    @GetMapping("/inquiry/{inquiryId}")
    public String inquiryDetail(@PathVariable Integer inquiryId, 
                               HttpSession session, 
                               Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Inquiry inquiry = inquiryService.getInquiryById(inquiryId);
        List<Comment> comments = inquiryService.getCommentsByInquiryId(inquiryId);
        
        model.addAttribute("inquiry", inquiry);
        model.addAttribute("comments", comments);
        model.addAttribute("adminId", adminId);
        
        return "admin/inquiry-detail";
    }
    
    /**
     * 문의에 답변 추가
     */
    @PostMapping("/inquiry/{inquiryId}/comment")
    @ResponseBody
    public String addComment(@PathVariable Integer inquiryId,
                           @RequestParam String content,
                           HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "error: 로그인이 필요합니다.";
        }
        
        try {
            inquiryService.addComment(inquiryId, adminId, content);
            return "success";
        } catch (Exception e) {
            return "error: 답변 등록 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
}
