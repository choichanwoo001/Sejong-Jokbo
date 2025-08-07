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

### 로컬 파일 저장 방식 사용 (Google Cloud Storage 없이 개발)

로컬에서 개발할 때 Google Cloud Storage 설정 없이 파일 업로드 기능을 테스트하려면:

1. **환경 변수 설정**:
   ```bash
   # Windows PowerShell
   $env:USE_LOCAL_STORAGE="true"
   
   # 또는 .env 파일에 추가
   USE_LOCAL_STORAGE=true
   ```

2. **JokboService.java 수정**:
   - Google Cloud Storage 업로드 부분을 주석 처리
   - 로컬 파일 저장 부분의 주석을 해제

3. **BookController.java 수정**:
   - Google Cloud Storage 다운로드 부분을 주석 처리
   - 로컬 파일 다운로드 부분의 주석을 해제

이렇게 하면 로컬 개발 환경에서도 파일 업로드/다운로드 기능을 정상적으로 테스트할 수 있습니다.

