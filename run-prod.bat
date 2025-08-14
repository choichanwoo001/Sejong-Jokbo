@echo off
chcp 65001 >nul 2>&1
echo "프로덕션 환경에서 애플리케이션을 실행합니다..."

REM 인코딩 환경 변수 설정
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8
set GRADLE_OPTS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8

REM 환경 변수 설정 (실제 값으로 변경 필요)
set SPRING_PROFILES_ACTIVE=prod
set DATABASE_URL=jdbc:mysql://your-prod-db-host:3306/sejong_jokbo?useSSL=true&serverTimezone=UTC&characterEncoding=UTF-8
set DB_USERNAME=your-prod-db-username
set DB_PASSWORD=your-prod-db-password
set GMAIL_USERNAME=your-email@gmail.com
set GMAIL_APP_PASSWORD=your-app-password
set GOOGLE_CLOUD_PROJECT_ID=your-gcp-project-id
set GOOGLE_CLOUD_STORAGE_BUCKET=your-gcs-bucket-name
set GOOGLE_CLOUD_CREDENTIALS_FILE=classpath:gcp-credentials.json
set PORT=8080

REM 애플리케이션 실행
./gradlew bootRun

pause
