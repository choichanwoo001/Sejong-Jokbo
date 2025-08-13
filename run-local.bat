@echo off
echo "로컬 환경에서 애플리케이션을 실행합니다..."

REM 환경 변수 설정
set SPRING_PROFILES_ACTIVE=local
set DB_USERNAME=root
set DB_PASSWORD=password
set GMAIL_USERNAME=your-email@gmail.com
set GMAIL_APP_PASSWORD=your-app-password

REM 애플리케이션 실행
./gradlew bootRun

pause
