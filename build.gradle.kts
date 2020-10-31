import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20-M1"
    id("com.github.ben-manes.versions") version "0.33.0"
}

group = "de.f0x.lokales"
version = "0.0.1"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation("io.kotest:kotest-runner-junit5:4.3.0")
    testImplementation("io.kotest:kotest-assertions-core:4.3.0")
    testImplementation("io.kotest:kotest-property:4.3.0")
}

val javaVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    withType<Test> {
        useJUnitPlatform()
    }
}