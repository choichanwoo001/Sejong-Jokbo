package com.sejong.controller;

import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.service.BookService;
import com.sejong.service.JokboService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
     * 책 상세 페이지를 보여줍니다
     */
    @GetMapping("/book/{bookId}")
    public String bookDetail(@PathVariable Integer bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        List<Jokbo> approvedJokbos = jokboService.getApprovedJokbosByBookId(bookId);
        
        model.addAttribute("book", book);
        model.addAttribute("jokbos", approvedJokbos);
        
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
        try {
            jokboService.registerTextJokbo(bookId, uploaderName, content, comment);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
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
            jokboService.registerFileJokbo(bookId, uploaderName, file, comment);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    /**
     * 족보 파일을 다운로드합니다
     */
    @GetMapping("/jokbo/download/{filename}")
    public ResponseEntity<Resource> downloadJokboFile(@PathVariable String filename) {
        try {
            Path filePath = jokboService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
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
     * 관리자용 족보 관리 페이지
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
     * 족보를 승인합니다
     */
    @PostMapping("/admin/jokbo/{jokboId}/approve")
    @ResponseBody
    public String approveJokbo(@PathVariable Integer jokboId) {
        try {
            jokboService.approveJokbo(jokboId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
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
            return "error: " + e.getMessage();
        }
    }
} 