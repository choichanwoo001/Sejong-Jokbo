# Java 21 기반 이미지 사용
FROM eclipse-temurin:21-jre-jammy

# curl 설치 (healthcheck용)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (빌드된 파일)
COPY build/libs/Sejong_Jokbo-0.0.1-SNAPSHOT.jar app.jar

# 업로드 디렉토리 생성
RUN mkdir -p /app/uploads/jokbo

# UTF-8 인코딩 설정
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

