FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# curl 설치 (healthcheck용)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 빌드된 JAR 파일 복사 (GitHub Actions에서 빌드된 파일)
COPY build/libs/*SNAPSHOT.jar app.jar

# 업로드 디렉토리 생성
RUN mkdir -p /app/uploads/jokbo

# 환경 변수 설정
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    JAVA_OPTS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

