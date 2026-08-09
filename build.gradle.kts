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
        // TODO: FIX THE SCRIPT CALL ON WINDOWS
        commandLine("cmd", "/c", "Powershell -File .\\scripts\\build-images.ps1 authenticator-service game-server-manager matchmaker pacman-game queries-service")
    } else {
        commandLine("bash", "-c", "./scripts/build-images.sh authenticator-service game-server-manager matchmaker pacman-game queries-service")
    }
}

// TODO: TEST THESE TASKS
tasks.register<Exec>("deployCluster") {
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "kubectl apply -f ./kubernetes/")
    } else {
        commandLine("bash", "-c", "kubectl apply -f ./kubernetes/")
    }
}

tasks.register<Exec>("cleanupCluster") {
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "kubectl delete -f ./kubernetes/")
    } else {
        commandLine("bash", "-c", "kubectl delete -f ./kubernetes/")
    }
}
