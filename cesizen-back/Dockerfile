# Étape 1 : compilation Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B


# Étape 2 : exécution
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build \
  /app/target/cesizen-back-0.0.1-SNAPSHOT.jar \
  app.jar

RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
