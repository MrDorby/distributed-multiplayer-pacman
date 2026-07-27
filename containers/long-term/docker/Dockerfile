# Dockerfile for the Queries Service
FROM eclipse-temurin:26
ARG JAR_FILE=../../../queries-service/build/libs/*.jar
COPY ${JAR_FILE} queries.jar
ENTRYPOINT ["java","-jar","/queries.jar"]