plugins {
}

group = "it.unibo"
//version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.register("build") {
    dependsOn(":pacman-game:buildShadow")
    dependsOn(":authenticator-service:build")
    dependsOn(":game-server-manager:build")
    dependsOn(":matchmaker:build")
    dependsOn(":queries-service:build")
}

tasks.register<Exec>("buildImages") {
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("git", "bash", "-c", "./build-images.sh authenticator-service game-server-manager matchmaker pacman-game queries-service")
    } else {
        commandLine("bash", "-c", "./build-images.sh authenticator-service game-server-manager matchmaker pacman-game queries-service")
    }
}

// TODO: CREATE A TASK THAT DEPLOYS THE WHOLE PROJECT IN THE CLUSTER
