plugins {
    kotlin("jvm") version "1.6.0"
    application
}

group = "io.github.kmakma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

//tasks.named<Wrapper>("wrapper") {
//    gradleVersion = "6.7.1"
//}

application {
    mainClass.set("io.github.kmakma.adventofcode.MainKt")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("com.github.ajalt:mordant:1.2.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}