import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.ben-manes.versions") version "0.33.0"
}

group = "de.f0x.lokales"
version = "0.0.1"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test-junit5"))
}

val javaVersion = JavaVersion.VERSION_1_8

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion.toString()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}