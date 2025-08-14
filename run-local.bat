@echo off
chcp 65001 >nul 2>&1
echo "로컬 환경에서 애플리케이션을 실행합니다..."

REM 인코딩 환경 변수 설정
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8
set GRADLE_OPTS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8

REM 환경 변수 설정
set SPRING_PROFILES_ACTIVE=local
set DB_USERNAME=root
set DB_PASSWORD=password
set GMAIL_USERNAME=your-email@gmail.com
set GMAIL_APP_PASSWORD=your-app-password

REM 애플리케이션 실행
./gradlew bootRun

pause
