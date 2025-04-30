FROM eclipse-temurin:21-jdk as builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

FROM eclipse-temurin:21-jdk
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]