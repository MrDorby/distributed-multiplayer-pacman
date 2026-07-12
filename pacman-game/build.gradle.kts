plugins {
    java
    application
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("tools.jackson.core:jackson-databind:3.1.2")
    // Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-cbor
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.22.0")
    // Source: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.32")
    // Source: https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:4.2.15.Final")
    // Source: https://mvnrepository.com/artifact/io.javalin/javalin
    implementation("io.javalin:javalin:7.2.2")
    // Source: https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.2") //TODO: check io.jsonwebtoken

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
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

tasks.register("buildShadow") {
    group = "build"
    description = "Dynamically builds shadow jars based on -PtargetJar flag"
    when (val target = project.findProperty("targetJar")?.toString()) {
        "server" -> dependsOn(shadowServer)
        "client" -> dependsOn(shadowClient)
        "all" -> dependsOn(shadowServer, shadowClient)
        null -> {
            logger.lifecycle("No -PtargetJar specified. Defaulting to building ALL jars.")
            dependsOn(shadowServer, shadowClient)
        }
        else -> throw GradleException("Unknown targetJar: '$target'. Allowed values: server, client, all")
    }
}

application {
    mainClass.set("it.unibo.GameServerMain")
}

tasks.test {
    useJUnitPlatform()
}