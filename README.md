# 세종족보 (Sejong Jokbo)

세종대학교 족보 관리 시스템

## 기술 스택

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Thymeleaf**
- **Lombok**
- **MySQL**
- **Gradle**
- **Jakarta EE**

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── sejong/
│   │           ├── SejongJokboApplication.java
│   │           ├── controller/
│   │           │   ├── HomeController.java
│   │           │   └── MemberController.java
│   │           ├── entity/
│   │           │   └── Member.java
│   │           ├── repository/
│   │           │   └── MemberRepository.java
│   │           └── service/
│   │               └── MemberService.java
│   └── resources/
│       ├── application.yml
│       └── templates/
│           ├── home.html
│           └── member/
│               ├── list.html
│               ├── form.html
│               └── detail.html
```

## 설정

### 데이터베이스 설정

1. MySQL 데이터베이스 생성:
```sql
CREATE DATABASE sejong_jokbo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 환경 변수 설정:
   - `.env` 파일 생성 후 데이터베이스 비밀번호 설정:
   ```
   DB_PASSWORD=your_password_here
   ```

### 애플리케이션 실행

1. 프로젝트 빌드:
```bash
./gradlew build
```

2. 애플리케이션 실행:
```bash
./gradlew bootRun
```

3. 브라우저에서 접속:
   - http://localhost:8080

## 주요 기능

- 회원 관리 (등록, 조회, 수정, 삭제)
- Thymeleaf를 이용한 웹 인터페이스
- JPA를 이용한 데이터베이스 연동
- Bootstrap을 이용한 반응형 UI

## API 엔드포인트

- `GET /` - 홈 페이지
- `GET /members` - 회원 목록
- `GET /members/new` - 회원 등록 폼
- `POST /members` - 회원 등록
- `GET /members/{id}` - 회원 상세 조회
- `GET /members/{id}/edit` - 회원 수정 폼
- `PUT /members/{id}` - 회원 수정
- `DELETE /members/{id}` - 회원 삭제 