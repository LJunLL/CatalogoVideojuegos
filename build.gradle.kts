plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Driver JDBC de PostgreSQL
    implementation("org.postgresql:postgresql:42.7.4")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}

// Para que readln() funcione al lanzar con "gradle run"
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
