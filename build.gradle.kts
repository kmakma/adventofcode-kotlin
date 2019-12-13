import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    application
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("com.github.ajalt:mordant:1.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

//tasks.register<Jar>("uberJar") {
//    group = "build"
//    manifest.attributes(
//        "Main-Class" to "io.github.kmakma.adventofcode.MainKt",
//        "Implementation-Title" to project.name,
//        "Implementation-Version" to project.version
//    )
//
//    archiveClassifier.set("uber")
//
//    from(sourceSets.main.get().output)
//
//    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    })
//}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}