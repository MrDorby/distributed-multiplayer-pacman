FROM eclipse-temurin:25
RUN mkdir "/opt/app"
COPY build/libs/front-end-1.0.jar /opt/app
ENTRYPOINT ["java", "-jar", "/opt/app/front-end-1.0.jar"]
