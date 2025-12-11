package com.sejong;

import com.sejong.entity.Book;
import com.sejong.entity.Jokbo;
import com.sejong.repository.BookRepository;
import com.sejong.repository.JokboRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@Slf4j
public class SejongJokboApplication {

    public static void main(String[] args) {
        SpringApplication.run(SejongJokboApplication.class, args);
    }

    @Bean
    public ApplicationRunner initUploadDir(@Value("${app.upload.path}") String uploadPathStr,
            @Value("${app.init-jokbo-count:false}") boolean initJokboCount,
            BookRepository bookRepository,
            JokboRepository jokboRepository) {
        return args -> {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadPathStr).resolve("jokbo/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("업로드 디렉토리가 생성되었습니다: {}", uploadPath.toAbsolutePath());
            }

            // 족보 수 초기화 (옵션)
            if (initJokboCount) {
                log.info("책 별 족보 수 업데이트 중...");
                List<Book> books = bookRepository.findAll();
                for (Book book : books) {
                    long count = jokboRepository.countByBookIdAndStatus(book.getBookId(), Jokbo.JokboStatus.승인);
                    book.setJokboCount((int) count);
                    bookRepository.save(book);
                }
                log.info("책 별 족보 수 업데이트 완료!");
            } else {
                // 기본적으로는 실행하지 않음 (서버 재시작마다 실행되면 비효율적일 수 있음)
                // 필요한 경우 application.yml에서 app.init-jokbo-count=true로 설정하여 실행
                log.info("족보 수 초기화 건너뜀 (INIT_JOKBO_COUNT=true로 설정하면 실행)");
            }
        };
    }
}