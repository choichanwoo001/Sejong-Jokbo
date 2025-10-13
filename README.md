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
- **Springdoc OpenAPI (Swagger UI 3)**
- **Cloudflare R2 / Google Cloud Storage** (파일 저장)
- **Apache PDFBox**
- **Spring Boot Mail**

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

- 도서 검색 및 족보 관리
- 족보 업로드 (텍스트 및 파일)
- 관리자 승인 시스템
- 문의 게시판
- 이메일 인증
- PDF 변환 및 다운로드
- Thymeleaf를 이용한 웹 인터페이스
- JPA를 이용한 데이터베이스 연동
- Bootstrap을 이용한 반응형 UI

## Swagger UI - API 문서

애플리케이션 실행 후 아래 주소에서 모든 API를 테스트할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### 사용 가능한 API 그룹

1. **페이지 관련 API** - 홈페이지 등 정적 페이지
2. **도서 및 족보 API** - 도서 검색, 족보 등록/다운로드
3. **문의 API** - 문의 게시판 관련 기능
4. **이메일 API** - 이메일 인증 관련 기능
5. **관리자 API** - 관리자 전용 기능

### 주요 API 엔드포인트

#### 공개 API
- `GET /` - 홈 페이지
- `GET /search` - 도서 검색
- `GET /book/{bookId}` - 도서 상세 페이지
- `POST /book/{bookId}/jokbo/text` - 텍스트 족보 등록
- `POST /book/{bookId}/jokbo/file` - 파일 족보 등록
- `GET /jokbo/download/{filename}` - 족보 파일 다운로드
- `GET /inquiry` - 문의 목록
- `POST /inquiry` - 문의 등록

#### 관리자 API
- `POST /admin/login` - 관리자 로그인
- `GET /admin/dashboard` - 관리자 대시보드
- `GET /admin/jokbos/pending` - 승인 대기 족보 목록
- `POST /admin/jokbo/{jokboId}/approve` - 족보 승인
- `POST /admin/jokbo/{jokboId}/reject` - 족보 반려

#### 이메일 API
- `POST /api/send-verification` - 인증번호 발송
- `POST /api/verify-code` - 인증번호 확인 

## Docker를 이용한 실행

Docker가 설치되어 있는 환경에서는 아래의 명령어를 통해 간편하게 애플리케이션을 실행할 수 있습니다.

### 1. Docker 이미지 빌드

프로젝트의 루트 디렉토리에서 다음 명령어를 실행하여 Docker 이미지를 빌드합니다.

```bash
docker build -t sejong-jokbo .
```

### 2. Docker 컨테이너 실행

빌드된 이미지를 사용하여 컨테이너를 실행합니다. 애플리케이션이 동작하기 위해서는 데이터베이스 및 이메일 연동을 위한 환경 변수 설정이 필요합니다.

```bash
docker run -p 8080:8080 \
       -e DB_USERNAME=your_db_username \
       -e DB_PASSWORD=your_db_password \
       -e GMAIL_USERNAME=your_gmail_username \
       -e GMAIL_APP_PASSWORD=your_gmail_app_password \
       sejong-jokbo
```

- `-p 8080:8080`: 로컬 컴퓨터의 8080 포트와 Docker 컨테이너의 8080 포트를 연결합니다.
- `-e`: 컨테이너 내부에서 사용할 환경 변수를 설정합니다. `your_...` 부분은 실제 값으로 변경해야 합니다.

### 3. 브라우저에서 접속

컨테이너가 정상적으로 실행되면 브라우저에서 `http://localhost:8080` 주소로 접속하여 애플리케이션을 확인할 수 있습니다.

## 로컬 개발 환경 설정

### 로컬 파일 저장 방식 사용 (클라우드 스토리지 없이 개발)

로컬에서 개발할 때는 자동으로 로컬 파일 시스템을 사용합니다:

1. **프로필 설정**: `application-local.yml`이 자동으로 사용됨
2. **파일 저장 위치**: `uploads/jokbo/` 디렉토리
3. **추가 설정 불필요**: 자동으로 LocalStorageService 사용

### 프로덕션 환경 (AWS EC2)

프로덕션 환경에서는 `application-prod.yml`이 사용되며:

1. **파일 저장**: Cloudflare R2
2. **데이터베이스**: MySQL (EC2 내부 또는 RDS)
3. **환경 변수**: 시스템 환경 변수로 설정

