# Dockerfile for the Matchmaker Service
FROM eclipse-temurin:26
ARG JAR_FILE=build/libs/matchmaker-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} matchmaker.jar
ENTRYPOINT ["java","-jar","/matchmaker.jar"]
