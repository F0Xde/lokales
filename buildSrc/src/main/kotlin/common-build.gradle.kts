import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "de.f0x.lokales"

repositories {
    jcenter()
    google()
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