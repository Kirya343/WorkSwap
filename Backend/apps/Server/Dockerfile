FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar
COPY config ./config

ENTRYPOINT ["java", "-jar", "app.jar"]