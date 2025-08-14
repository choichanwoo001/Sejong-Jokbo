package com.sejong;

import com.sejong.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class SejongJokboApplication {

    public static void main(String[] args) {
        SpringApplication.run(SejongJokboApplication.class, args);
    }
    
    /**
     * 애플리케이션 시작 시 초기화 작업을 수행합니다
     */
    @Component
    public static class ApplicationInitializer implements CommandLineRunner {
        
        @Autowired
        private BookService bookService;
        
        @Override
        public void run(String... args) throws Exception {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get("uploads/jokbo/");
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                    System.out.println("업로드 디렉토리가 생성되었습니다: " + uploadPath.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("업로드 디렉토리 생성에 실패했습니다: " + e.getMessage());
                }
            }
            
            // 모든 책의 jokboCount 초기화
            try {
                System.out.println("책 별 족보 수 업데이트 중...");
                bookService.updateAllJokboCounts();
                System.out.println("책 별 족보 수 업데이트 완료!");
            } catch (Exception e) {
                System.err.println("족보 수 업데이트 실패: " + e.getMessage());
            }
        }
    }
} 