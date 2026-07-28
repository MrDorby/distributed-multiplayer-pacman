FROM eclipse-temurin:25
RUN mkdir "/opt/app"
COPY build/libs/pacman-game-1.0-server.jar /opt/app
ENTRYPOINT ["java", "-jar", "/opt/app/pacman-game-1.0-server.jar"]
# CMD [] TODO: put some default arguments in CMD?
