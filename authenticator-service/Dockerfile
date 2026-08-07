# Dockerfile for the Authenticator Service
FROM eclipse-temurin:26
ARG JAR_FILE=build/libs/authenticator-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} authenticator.jar
ENTRYPOINT ["java","-jar","/authenticator.jar"]
