# Stage 1: 빌드 환경
# Java 21과 Gradle을 포함한 이미지를 기반으로 빌드 단계를 설정합니다.
FROM gradle:8.5.0-jdk21-jammy AS builder

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 먼저 의존성 관련 파일만 복사하여 Gradle의 캐싱을 활용합니다.
# 이렇게 하면 소스 코드가 변경되지 않았을 경우, 의존성을 다시 다운로드하지 않아 빌드 속도가 향상됩니다.
COPY build.gradle gradlew ./
COPY gradle ./gradle

# 의존성을 미리 다운로드합니다. --go-offline 옵션으로 네트워크 없이 빌드 가능하도록 준비합니다.
RUN gradle build --go-offline || true

# 전체 소스 코드를 복사합니다.
COPY src ./src

# 애플리케이션을 빌드합니다. 테스트는 제외하여 빌드 시간을 단축합니다.
RUN gradle build -x test

# Stage 2: 실행 환경
# 더 가볍고 안전한 JRE(Java Runtime Environment) 이미지를 기반으로 최종 이미지를 만듭니다.
FROM eclipse-temurin:21-jre-jammy

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 단계(builder)에서 생성된 JAR 파일을 복사합니다.
# build/libs/ 디렉토리 아래에 생성된 JAR 파일 중 이름이 'sejong-jokbo'로 시작하고 '.jar'로 끝나는 파일을 app.jar로 복사합니다.
COPY --from=builder /app/build/libs/sejong-jokbo-*.jar app.jar

# 애플리케이션이 사용할 포트를 8080으로 노출시킵니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행할 명령을 정의합니다.
# java -jar app.jar 명령으로 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

