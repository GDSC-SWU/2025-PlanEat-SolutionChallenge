FROM eclipse-temurin:21-jdk as builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jdk

EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar /app.jar"]
