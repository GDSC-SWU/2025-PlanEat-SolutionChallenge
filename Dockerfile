FROM eclipse-temurin:21-jdk as builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test --no-daemon
RUN ls -alh /app/build/libs  # <- jar 파일 생겼는지 확인용

FROM eclipse-temurin:21-jdk

EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "/app.jar"]