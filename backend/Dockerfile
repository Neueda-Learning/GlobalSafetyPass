FROM maven:3.8.1-openjdk-17 as builder

WORKDIR /app
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline

COPY backend/ .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/globalsafetypass-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
