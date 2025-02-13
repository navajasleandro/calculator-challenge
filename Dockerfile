FROM openjdk:21-jdk-slim as builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/backend-*.jar app.jar

EXPOSE 8080

RUN addgroup --system appgroup && adduser --system appuser && chown -R appuser:appgroup /app
USER appuser

CMD ["java", "-jar", "app.jar"]
