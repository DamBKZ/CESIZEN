# Étape 1 : compilation Maven
FROM maven:3-eclipse-temurin-26-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY checkstyle.xml ./

RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src

RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests


# Étape 2 : exécution
FROM eclipse-temurin:21-jre-alpine

RUN apk upgrade --no-cache \
    && apk add --no-cache wget \
    && addgroup --system spring \
    && adduser --system --ingroup spring spring

WORKDIR /app

COPY --from=build --chown=spring:spring \
    /app/target/cesizen-back-0.0.1-SNAPSHOT.jar \
    /app/app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
