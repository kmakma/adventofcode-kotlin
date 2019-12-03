import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.61"
}

group = "io.github.kmakma"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

application {
    mainClassName = "io.github.kmakma.adventofcode.MainKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
