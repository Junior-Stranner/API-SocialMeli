FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests clean package spring-boot:repackage

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie o JAR executável (não o original-*.jar)
COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]