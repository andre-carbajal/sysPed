FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

