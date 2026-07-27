plugins {
    java
    application
    id("com.gradleup.shadow") version "9.5.1"
}

group = "it.unibo"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    // Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-cbor
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.22.1")
    // Source: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.32")
    // Source: https://mvnrepository.com/artifact/io.netty/netty-transport
    implementation("io.netty:netty-transport:4.2.16.Final")
    // Source: https://mvnrepository.com/artifact/io.netty/netty-codec-http2
    implementation("io.netty:netty-codec-http2:4.2.16.Final")
    // Source: https://mvnrepository.com/artifact/org.mockito/mockito-core
    testImplementation("org.mockito:mockito-core:5.23.0")
    // Source: https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.2")

    implementation(platform("org.mongodb:mongodb-driver-bom:5.9.0"))
    implementation(platform("io.projectreactor:reactor-bom:2025.0.6"))
    implementation("io.projectreactor:reactor-core")
    implementation("org.mongodb:mongodb-driver-reactivestreams")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

/*
 * How to build custom build tasks: https://gradleup.com/shadow/custom-tasks/
 */

val shadowServer = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowServer") {
    group = "shadow"
    description = "Builds the GameServer Fat JAR"
    archiveClassifier.set("server")
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
    manifest {
        attributes(mapOf("Main-Class" to "it.unibo.GameServerMain"))
    }
}

val shadowClient = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowClient") {
    group = "shadow"
    description = "Builds the GameClient Fat JAR"
    archiveClassifier.set("client")
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
    manifest {
        attributes("Main-Class" to "it.unibo.GameClientMain")
    }
}

val shadowFullClient = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowFullClient") {
    group = "shadow"
    description = "Builds the FullGameClient Fat JAR"
    archiveClassifier.set("full-client")
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
    manifest {
        attributes("Main-Class" to "it.unibo.FullPacmanClientMain")
    }
}

tasks.register("buildShadow") {
    group = "build"
    description = "Dynamically builds shadow jars based on -PtargetJar flag"
    when (val target = project.findProperty("targetJar")?.toString()) {
        "server" -> dependsOn(shadowServer)
        "client" -> dependsOn(shadowClient)
        "full-client" -> dependsOn(shadowFullClient)
        "all" -> dependsOn(shadowServer, shadowClient, shadowFullClient)
        null -> {
            logger.lifecycle("No -PtargetJar specified. Defaulting to building ALL jars.")
            dependsOn(shadowServer, shadowClient, shadowFullClient)
        }
        else -> throw GradleException("Unknown targetJar: '$target'. Allowed values: server, client, full-client, all")
    }
}

application {
    mainClass.set("it.unibo.GameServerMain")
}

tasks.test {
    useJUnitPlatform()
}