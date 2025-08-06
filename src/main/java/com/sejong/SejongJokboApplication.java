package com.sejong;

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
     * 애플리케이션 시작 시 업로드 디렉토리를 생성합니다
     */
    @Component
    public static class UploadDirectoryInitializer implements CommandLineRunner {
        
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
        }
    }
} 