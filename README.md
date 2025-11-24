# 세종족보 (Sejong Jokbo)

세종대학교 족보 관리 시스템입니다.  
현재 저장소는 **로컬 PC/개인 서버**에서 바로 실행할 수 있도록 구성되어 있으며,
파일 업로드 역시 로컬 디렉터리(`uploads/jokbo/`)를 사용합니다.

## 기술 스택

- Java 21
- Spring Boot 3.2
- Spring Data JPA & Hibernate
- Thymeleaf
- Spring Validation
- Spring Mail
- Lombok
- MySQL
- Apache PDFBox
- Springdoc OpenAPI (Swagger UI)
- Gradle

## 주요 기능

- 도서/족보 검색 및 상세 조회
- 족보 업로드 (텍스트·파일) 및 관리자 승인 흐름
- 족보 승인 이력 관리
- 문의 게시판 & 관리자 댓글
- 이메일 인증 (선택 구성)
- 텍스트 족보 → PDF 변환
- SSE 기반 실시간 알림

## 프로젝트 구조 (요약)

```
src/main/java/com/sejong
├── SejongJokboApplication.java
├── config/
│   └── SwaggerConfig.java
├── controller/
│   ├── api/
│   └── view/
├── entity/
├── repository/
├── service/
│   ├── JokboService, InquiryService, ...
│   └── LocalStorageService (파일 저장)
└── global/
    ├── dto/
    └── exception/

src/main/resources
├── application.yml (기본 profile=local)
├── application-local.yml
├── templates/ (Thymeleaf)
└── static/ (CSS, JS, 이미지)
```

## 실행 방법

1. **데이터베이스 준비**
   ```sql
   CREATE DATABASE sejong_jokbo
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
   ```

2. **환경 변수(.env) 설정**
   ```bash
   cp env.example.txt .env
   # 필요한 DB 계정 및 메일 정보를 입력
   ```

3. **초기 더미 데이터(Optional)**  
   `init.sql`을 MySQL에 실행하면 기본 데이터가 채워집니다.

4. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   # 또는
   ./gradlew build
   java -jar build/libs/sejong-jokbo-0.0.1-SNAPSHOT.jar
   ```

5. **접속**
   - 웹: <http://localhost:8080>
   - Swagger UI: <http://localhost:8080/swagger-ui.html>
   - OpenAPI 문서: <http://localhost:8080/api-docs>

## 환경 & 파일 저장

- 기본 프로필은 `local`입니다. (다른 프로필 불필요)
- 업로드된 파일은 `uploads/jokbo/` 디렉터리에 저장됩니다.
- 필요 시 `.env`의 `SPRING_PROFILES_ACTIVE` 값을 변경하여 다른 설정 파일을 연결할 수 있습니다.

## 개발 팁

- Gradle 캐시를 활용하기 위해 `./gradlew` 실행 전 실행 권한을 부여하세요.  
  `chmod +x gradlew`
- 로컬에서 메일 전송을 사용하지 않을 경우, 관련 환경 변수를 비워두면 됩니다.
- 초기 데이터가 필요 없으면 `init.sql`을 실행하지 않아도 됩니다.
