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

// TODO: FIX THESE TASKS OR REMOVE THEM
//tasks.register<Exec>("buildImages") {
//    if (System.getProperty("os.name").lowercase().contains("windows")) {
//        // TODO: FIX THE SCRIPT CALL ON WINDOWS
//        commandLine("cmd", "/c", "powershell -command \".\\scripts\\build-images.ps1 authenticator-service game-server-manager matchmaker pacman-game queries-service\"")
//    } else {
//        commandLine("bash", "-c", "./scripts/build-images.sh authenticator-service game-server-manager matchmaker pacman-game queries-service")
//    }
//}
//
//// TODO: MODIFY THESE TASKS TO RUN THE RELATIVE SCRIPTS (kubectl apply and delete are not recursive on directories)
//tasks.register<Exec>("deployCluster") {
//    if (System.getProperty("os.name").lowercase().contains("windows")) {
//        commandLine("cmd", "/c", "kubectl apply -f ./kubernetes/")
//    } else {
//        commandLine("bash", "-c", "kubectl apply -f ./kubernetes/")
//    }
//}
//
//tasks.register<Exec>("cleanupCluster") {
//    if (System.getProperty("os.name").lowercase().contains("windows")) {
//        commandLine("cmd", "/c", "kubectl delete -f ./kubernetes/")
//    } else {
//        commandLine("bash", "-c", "kubectl delete -f ./kubernetes/")
//    }
//}
