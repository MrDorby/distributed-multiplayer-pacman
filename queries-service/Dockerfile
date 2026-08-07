FROM eclipse-temurin:26
RUN mkdir "/opt/app"
COPY build/libs/queries-service-1.0-SNAPSHOT.jar /opt/app/queries-service.jar
ENTRYPOINT ["java", "-jar", "/opt/app/queries-service.jar"]
