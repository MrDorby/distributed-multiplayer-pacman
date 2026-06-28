plugins {
    java
    application
}

group = "it.unibo"
version = "1.0-SNAPSHOT"

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
    // Source: https://mvnrepository.com/artifact/com.esotericsoftware/kryonet
    implementation("com.esotericsoftware:kryonet:2.22.0-RC1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

application {
    mainClass.set("it.unibo.Main")
}

tasks.test {
    useJUnitPlatform()
}