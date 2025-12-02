package com.sejong.controller.view;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "도서 뷰", description = "도서 검색 및 상세 페이지 관련")
@Slf4j
public class BookViewController {

    private final BookService bookService;
    private final JokboService jokboService;

    /**
     * 통합 검색을 수행합니다
     */
    @Operation(summary = "도서 통합 검색", description = "키워드로 도서를 검색합니다")
    @GetMapping("/search")
    public String searchBooks(@Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            Model model) {
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
    public String bookDetail(@PathVariable @org.springframework.lang.NonNull Integer bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String tab,
            Model model) {
        Book book = bookService.getBookById(bookId);
        Page<Jokbo> jokboPage = jokboService.getApprovedJokbosByBookId(bookId, page);

        model.addAttribute("book", book);
        model.addAttribute("translatedCategory", translateCategory(book.getCategory()));
        model.addAttribute("jokbos", jokboPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jokboPage.getTotalPages());
        model.addAttribute("hasNext", jokboPage.hasNext());
        model.addAttribute("hasPrevious", jokboPage.hasPrevious());
        model.addAttribute("activeTab", tab != null ? tab : "register");

        return "book/detail";
    }

    private String translateCategory(String category) {
        if (category == null) {
            return "";
        }
        switch (category.toLowerCase()) {
            case "east":
                return "동양";
            case "west":
                return "서양";
            case "eastwest":
                return "동서양";
            case "science":
                return "과학";
            default:
                return category;
        }
    }

    /**
     * 족보 파일을 다운로드합니다 (환경에 따라 자동 선택)
     */
    @GetMapping("/jokbo/download/{filename:.+}")
    public ResponseEntity<Resource> downloadJokboFile(@PathVariable String filename) {
        try {
            // 파일명에서 족보 ID 추출을 시도하거나, 별도의 로직으로 족보를 찾아야 함
            // 현재 구조상 filename만으로는 족보 ID를 알기 어려움
            // 따라서 JokboService에서 filename으로 족보를 찾는 메서드가 필요하거나,
            // 다운로드 URL에 jokboId를 포함시켜야 함.
            // 기존 API 유지를 위해 filename으로 족보를 찾는 방식을 사용하거나,
            // 일단 여기서는 파일 다운로드 카운트를 증가시키지 못하는 한계가 있음.
            // 하지만 구현 계획에 따라 진행해야 하므로, filename으로 족보를 찾는 메서드를 추가하는 것이 좋겠음.
            // 일단은 텍스트 족보 다운로드 부분만 적용하고, 파일 다운로드는 추후 보완하거나
            // JokboService에 filename으로 Jokbo를 찾는 메서드를 추가해야 함.

            // JokboService에 findByContentUrl 메서드를 추가하여 해결
            Jokbo jokbo = jokboService.getJokboByContentUrl(filename);
            if (jokbo != null) {
                jokboService.increaseDownloadCount(java.util.Objects.requireNonNull(jokbo.getJokboId()));
            }

            // 로컬 환경에서는 getFilePath, GCP 환경에서는 downloadFile 사용
            try {
                Path filePath = jokboService.getFilePath(filename);
                Resource resource = new UrlResource(
                        java.util.Objects.requireNonNull(filePath.toAbsolutePath().toUri()));

                if (resource.exists() && resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (UnsupportedOperationException e) {
                // GCP 환경인 경우 downloadFile 사용
                Resource resource = jokboService.getFileStorageService().downloadFile(filename);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            }
        } catch (Exception e) {
            log.error("족보 파일 다운로드 중 오류 발생: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 텍스트 족보를 PDF로 보기 (브라우저에서 열기)
     */
    @GetMapping("/jokbo/view/text/{jokboId}")
    public ResponseEntity<byte[]> viewTextJokboAsPdf(@PathVariable @org.springframework.lang.NonNull Integer jokboId) {
        try {
            byte[] pdfBytes = jokboService.getTextJokboAsPdf(jokboId);

            return ResponseEntity.ok()
                    .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_PDF))
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("텍스트 족보 PDF 보기 중 오류 발생: {}", jokboId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 텍스트 족보를 PDF로 다운로드합니다
     */
    @GetMapping("/jokbo/download/text/{jokboId}")
    public ResponseEntity<byte[]> downloadTextJokboAsPdf(
            @PathVariable @org.springframework.lang.NonNull Integer jokboId) {
        try {
            jokboService.increaseDownloadCount(jokboId);
            byte[] pdfBytes = jokboService.getTextJokboAsPdf(jokboId);

            String filename = "jokbo_" + jokboId + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_PDF))
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("텍스트 족보 PDF 다운로드 중 오류 발생: {}", jokboId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 족보 파일을 뷰어에서 보여줍니다
     */
    @GetMapping("/jokbo/view/{filename:.+}")
    public ResponseEntity<Resource> viewJokboFile(@PathVariable String filename) {
        try {
            // GCP 환경에서는 getFilePath가 지원되지 않으므로 downloadFile 사용
            try {
                Path filePath = jokboService.getFilePath(filename);
                Resource resource = new UrlResource(
                        java.util.Objects.requireNonNull(filePath.toAbsolutePath().toUri()));

                if (resource.exists() && resource.isReadable()) {
                    String contentType = getContentType(filename);
                    return ResponseEntity.ok()
                            .contentType(java.util.Objects.requireNonNull(
                                    MediaType.parseMediaType(java.util.Objects.requireNonNull(contentType))))
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (UnsupportedOperationException e) {
                // GCP 환경인 경우 downloadFile 사용
                Resource resource = jokboService.getFileStorageService().downloadFile(filename);
                String contentType = getContentType(filename);
                return ResponseEntity.ok()
                        .contentType(java.util.Objects.requireNonNull(
                                MediaType.parseMediaType(java.util.Objects.requireNonNull(contentType))))
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("족보 파일 보기 중 오류 발생: {}", filename, e);
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

}
