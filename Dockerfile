FROM eclipse-temurin:21-jdk as builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jdk

# Cloud Run이 포트를 감지할 수 있도록 노출
EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot가 PORT 환경변수에 맞춰서 실행되도록 설정
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "/app.jar"]