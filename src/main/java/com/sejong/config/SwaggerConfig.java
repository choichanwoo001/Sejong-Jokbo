package com.sejong.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("세종 족보 API")
                        .description("세종대학교 족보 관리 시스템 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("세종 족보 팀")
                                .email("contact@sejong-jokbo.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 개발 서버"),
                        new Server().url("https://your-production-url.com").description("운영 서버")
                ));
    }
}
