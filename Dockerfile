# Stage 1: Build Module
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 의존성 캐싱을 위해 설정 파일만 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN chmod +x gradlew
# 의존성 다운로드 (소스 복사 전)
RUN ./gradlew dependencies --no-daemon || return 0

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew build -x test --no-daemon

# Stage 2: Run Module
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# curl 설치 (healthcheck용)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 빌드 스테이지에서 생성된 JAR 파일 복사
# 파일명이 버전에 따라 달라질 수 있으므로 와일드카드 사용 후 이름 변경 고려, 
# 하지만 여기서는 하나만 생성된다고 가정하고 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# 업로드 디렉토리 생성
RUN mkdir -p /app/uploads/jokbo

# 환경 변수 설정
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    JAVA_OPTS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

