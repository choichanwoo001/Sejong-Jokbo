package com.sejong.controller;

import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.service.BookService;
import com.sejong.service.JokboService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final JokboService jokboService;
    
    /**
     * 통합 검색을 수행합니다
     */
    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String keyword, Model model) {
        List<Book> searchResults = bookService.searchBooks(keyword);
        model.addAttribute("books", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isSearch", true);
        return "home";
    }
    
    /**
     * 카테고리별 검색을 수행합니다
     */
    @GetMapping("/search/category")
    public String searchBooksByCategory(@RequestParam String category, 
                                      @RequestParam(required = false) String keyword, 
                                      Model model) {
        List<Book> searchResults = bookService.searchBooksByCategoryAndKeyword(category, keyword);
        model.addAttribute("books", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("isSearch", true);
        return "home";
    }
    
    /**
     * 책 상세 페이지를 보여줍니다 (페이징 포함)
     */
    @GetMapping("/book/{bookId}")
    public String bookDetail(@PathVariable Integer bookId, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String tab,
                           Model model) {
        Book book = bookService.getBookById(bookId);
        Page<Jokbo> jokboPage = jokboService.getApprovedJokbosByBookId(bookId, page);
        
        model.addAttribute("book", book);
        model.addAttribute("jokbos", jokboPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jokboPage.getTotalPages());
        model.addAttribute("hasNext", jokboPage.hasNext());
        model.addAttribute("hasPrevious", jokboPage.hasPrevious());
        model.addAttribute("activeTab", tab != null ? tab : "register");
        
        return "book/detail";
    }
    
    /**
     * 텍스트 족보를 등록합니다
     */
    @PostMapping("/book/{bookId}/jokbo/text")
    @ResponseBody
    public String registerTextJokbo(@PathVariable Integer bookId,
                                   @RequestParam String uploaderName,
                                   @RequestParam String content,
                                   @RequestParam(required = false) String comment) {
        jokboService.registerTextJokbo(bookId, uploaderName, content, comment);
        return "success";
    }
    
    /**
     * 파일 족보를 등록합니다
     */
    @PostMapping("/book/{bookId}/jokbo/file")
    @ResponseBody
    public String registerFileJokbo(@PathVariable Integer bookId,
                                   @RequestParam String uploaderName,
                                   @RequestParam MultipartFile file,
                                   @RequestParam(required = false) String comment) {
        try {
            // 입력값 검증
            if (uploaderName == null || uploaderName.trim().isEmpty()) {
                return "error: 업로더 이름을 입력해주세요.";
            }
            
            if (file == null || file.isEmpty()) {
                return "error: 업로드할 파일을 선택해주세요.";
            }
            
            jokboService.registerFileJokbo(bookId, uploaderName.trim(), file, comment);
            return "success";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        } catch (java.io.IOException e) {
            return "error: 파일 처리 중 오류가 발생했습니다. - " + e.getMessage();
        } catch (RuntimeException e) {
            return "error: " + e.getMessage();
        } catch (Exception e) {
            return "error: 족보 등록 중 예상치 못한 오류가 발생했습니다. - " + e.getMessage();
        }
    }
    
    /**
     * 족보 파일을 다운로드합니다 (Google Cloud Storage 사용)
     */
    @GetMapping("/jokbo/download/{filename}")
    public ResponseEntity<Resource> downloadJokboFile(@PathVariable String filename) {
        try {
            // Google Cloud Storage 다운로드 (Docker 배포 시 사용)
            Resource resource = jokboService.getStorageService().downloadFile(filename);
            
            // 로컬 파일 다운로드 (개발 환경용)
            /*
            Path filePath = jokboService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            */
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 텍스트 족보를 PDF로 보기 (브라우저에서 열기)
     */
    @GetMapping("/jokbo/view/text/{jokboId}")
    public ResponseEntity<byte[]> viewTextJokboAsPdf(@PathVariable Integer jokboId) {
        try {
            byte[] pdfBytes = jokboService.getTextJokboAsPdf(jokboId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 텍스트 족보를 PDF로 다운로드합니다
     */
    @GetMapping("/jokbo/download/text/{jokboId}")
    public ResponseEntity<byte[]> downloadTextJokboAsPdf(@PathVariable Integer jokboId) {
        try {
            byte[] pdfBytes = jokboService.getTextJokboAsPdf(jokboId);
            
            String filename = "jokbo_" + jokboId + ".pdf";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 족보 파일을 뷰어에서 보여줍니다
     */
    @GetMapping("/jokbo/view/{filename}")
    public ResponseEntity<Resource> viewJokboFile(@PathVariable String filename) {
        try {
            Path filePath = jokboService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 파일 확장자에 따른 Content-Type을 반환합니다
     */
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }
    
    /**
     * 관리자용 족보 관리 페이지 (페이징 없음)
     */
    @GetMapping("/admin/book/{bookId}/jokbos")
    public String adminJokboManagement(@PathVariable Integer bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        List<Jokbo> allJokbos = jokboService.getAllJokbosByBookId(bookId);
        
        model.addAttribute("book", book);
        model.addAttribute("jokbos", allJokbos);
        
        return "admin/jokbo-management";
    }
    
    /**
     * 관리자용 족보 관리 페이지 (페이징 포함)
     */
    @GetMapping("/admin/book/{bookId}/jokbos/page/{page}")
    public String adminJokboManagementWithPaging(@PathVariable Integer bookId, 
                                                @PathVariable int page, 
                                                Model model) {
        Book book = bookService.getBookById(bookId);
        Page<Jokbo> jokboPage = jokboService.getAllJokbosByBookId(bookId, page);
        
        model.addAttribute("book", book);
        model.addAttribute("jokbos", jokboPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jokboPage.getTotalPages());
        model.addAttribute("hasNext", jokboPage.hasNext());
        model.addAttribute("hasPrevious", jokboPage.hasPrevious());
        
        return "admin/jokbo-management";
    }
    
    /**
     * 족보를 승인합니다
     */
    @PostMapping("/admin/jokbo/{jokboId}/approve")
    @ResponseBody
    public String approveJokbo(@PathVariable Integer jokboId) {
        try {
            jokboService.approveJokbo(jokboId);
            return "success";
        } catch (Exception e) {
            return "error: 족보 승인 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
    
    /**
     * 족보를 반려합니다
     */
    @PostMapping("/admin/jokbo/{jokboId}/reject")
    @ResponseBody
    public String rejectJokbo(@PathVariable Integer jokboId) {
        try {
            jokboService.rejectJokbo(jokboId);
            return "success";
        } catch (Exception e) {
            return "error: 족보 반려 중 오류가 발생했습니다. - " + e.getMessage();
        }
    }
} 