package com.sejong.controller.view;

import com.sejong.entity.Book;
import com.sejong.entity.Comment;
import com.sejong.entity.Inquiry;
import com.sejong.entity.Jokbo;
import com.sejong.entity.JokboApprovalHistory;
import com.sejong.service.AdminService;
import com.sejong.service.BookService;
import com.sejong.service.InquiryService;
import com.sejong.service.JokboService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 뷰", description = "관리자 페이지 관련")
public class AdminViewController {
    
    private final AdminService adminService;
    private final BookService bookService;
    private final JokboService jokboService;
    private final InquiryService inquiryService;
    
    /**
     * 관리자 로그인 페이지
     */
    @Operation(summary = "관리자 로그인 페이지", description = "관리자 로그인 페이지를 반환합니다")
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
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
     * 족보 승인 이력 조회 (페이징 및 필터링)
     */
    @GetMapping("/jokbo/{jokboId}/history")
    public String jokboHistory(@PathVariable Integer jokboId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "15") int size,
                              @RequestParam(required = false) String action,
                              @RequestParam(required = false) String previousStatus,
                              @RequestParam(required = false) String newStatus,
                              HttpSession session,
                              Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Page<JokboApprovalHistory> historyPage = adminService.getJokboApprovalHistoryWithFilters(
            jokboId, page, size, action, previousStatus, newStatus);
        Jokbo jokbo = jokboService.getJokboById(jokboId);
        
        model.addAttribute("jokbo", jokbo);
        model.addAttribute("history", historyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", historyPage.getTotalPages());
        model.addAttribute("hasNext", historyPage.hasNext());
        model.addAttribute("hasPrevious", historyPage.hasPrevious());
        model.addAttribute("selectedAction", action);
        model.addAttribute("selectedPreviousStatus", previousStatus);
        model.addAttribute("selectedNewStatus", newStatus);
        
        return "admin/jokbo-history";
    }
    
    /**
     * 모든 승인 이력 조회 (페이징 및 필터링)
     */
    @GetMapping("/approval-history")
    public String approvalHistory(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "15") int size,
                                 @RequestParam(required = false) String action,
                                 @RequestParam(required = false) String previousStatus,
                                 @RequestParam(required = false) String newStatus,
                                 HttpSession session,
                                 Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Page<JokboApprovalHistory> historyPage = adminService.getAllApprovalHistoryWithFilters(
            page, size, action, previousStatus, newStatus);
        
        model.addAttribute("history", historyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", historyPage.getTotalPages());
        model.addAttribute("hasNext", historyPage.hasNext());
        model.addAttribute("hasPrevious", historyPage.hasPrevious());
        model.addAttribute("selectedAction", action);
        model.addAttribute("selectedPreviousStatus", previousStatus);
        model.addAttribute("selectedNewStatus", newStatus);
        
        return "admin/approval-history";
    }
    
    /**
     * 관리자용 책 상세 페이지 (페이징 없음)
     */
    @GetMapping("/book/{bookId}/detail")
    public String adminBookDetail(@PathVariable Integer bookId, 
                                 HttpSession session, 
                                 Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Book book = bookService.getBookById(bookId);
        List<Jokbo> jokbos = jokboService.getApprovedJokbosByBookId(bookId);
        
        model.addAttribute("book", book);
        model.addAttribute("jokbos", jokbos);
        
        return "admin/book-detail";
    }
    
    /**
     * 관리자용 책 상세 페이지 (페이징 포함)
     */
    @GetMapping("/book/{bookId}/detail/page/{page}")
    public String adminBookDetailWithPaging(@PathVariable Integer bookId, 
                                           @PathVariable int page,
                                           HttpSession session, 
                                           Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Book book = bookService.getBookById(bookId);
        Page<Jokbo> jokboPage = jokboService.getApprovedJokbosByBookId(bookId, page);
        
        model.addAttribute("book", book);
        model.addAttribute("jokbos", jokboPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jokboPage.getTotalPages());
        model.addAttribute("hasNext", jokboPage.hasNext());
        model.addAttribute("hasPrevious", jokboPage.hasPrevious());
        
        return "admin/book-detail";
    }
    
    /**
     * 족보 관리 페이지 (모든 족보 - 승인, 반려, 대기 포함)
     */
    @GetMapping("/jokbos")
    public String allJokbos(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String status,
                           HttpSession session,
                           Model model) {
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        Page<Jokbo> jokboPage;
        if (status != null && !status.isEmpty()) {
            Jokbo.JokboStatus jokboStatus = Jokbo.JokboStatus.valueOf(status);
            jokboPage = jokboService.getJokbosByStatus(jokboStatus, page, size);
        } else {
            jokboPage = jokboService.getAllJokbos(page, size);
        }
        
        model.addAttribute("jokbos", jokboPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jokboPage.getTotalPages());
        model.addAttribute("hasNext", jokboPage.hasNext());
        model.addAttribute("hasPrevious", jokboPage.hasPrevious());
        model.addAttribute("selectedStatus", status);
        
        return "admin/jokbo-management";
    }
}
