plugins {
    java
    application
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.gradleup.shadow") version "9.5.1"
}

group = "it.unibo"
version = "1.0"

val mockitoAgent = configurations.create("mockitoAgent") {
    isTransitive = false
}

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
    // Source: https://mvnrepository.com/artifact/org.mockito/mockito-core
    mockitoAgent("org.mockito:mockito-core:5.23.0")

    // Source: https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.2") //TODO: check io.jsonwebtoken

    implementation(platform("org.mongodb:mongodb-driver-bom:5.9.0"))
    implementation(platform("io.projectreactor:reactor-bom:2025.0.6"))
    implementation("io.projectreactor:reactor-core")
    implementation("org.mongodb:mongodb-driver-reactivestreams")

    // Agones SDK dependencies
    // Base module
    implementation("net.infumia:agones4j:2.0.2")
    // Required, https://mvnrepository.com/artifact/io.grpc/grpc-stub/
    implementation("io.grpc:grpc-stub:1.64.0")
    implementation("io.grpc:grpc-protobuf:1.64.0")
    // Required, https://github.com/grpc/grpc-java/blob/master/gradle/libs.versions.toml#L46/
    implementation("org.apache.tomcat:annotations-api:6.0.53")
    // Source: https://mvnrepository.com/artifact/io.grpc/grpc-netty-shaded
    implementation("io.grpc:grpc-netty-shaded:1.82.2")
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
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
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
    jvmArgs.add("-javaagent:${mockitoAgent.asPath}")
}