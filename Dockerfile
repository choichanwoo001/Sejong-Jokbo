# Stage 1: 빌드 환경
# Java 21과 Gradle을 포함한 이미지를 기반으로 빌드 단계를 설정합니다.
FROM gradle:8.5.0-jdk21-jammy AS builder

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 먼저 의존성 관련 파일만 복사하여 캐시를 활용합니다.
# Gradle Wrapper를 사용해 버전 일치를 보장합니다.
COPY gradle ./gradle
COPY gradlew build.gradle ./
RUN chmod +x gradlew

# 의존성 프리페치 (네트워크 이슈가 있어도 빌드가 계속되도록 허용)
RUN ./gradlew dependencies --no-daemon || true

# 전체 소스 복사 후 빌드 (테스트 제외)
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: 실행 환경
# 더 가볍고 안전한 JRE(Java Runtime Environment) 이미지를 기반으로 최종 이미지를 만듭니다.
FROM eclipse-temurin:21-jre-jammy

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 단계에서 생성된 단일 부트 JAR을 복사합니다. (프로젝트명 변경에도 안전)
# 부트 JAR만 복사(plain JAR 제외). 버전이 SNAPSHOT이 아닐 경우 패턴을 조정하세요.
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# 애플리케이션이 사용할 포트를 8080으로 노출시킵니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행할 명령을 정의합니다.
# java -jar app.jar 명령으로 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

