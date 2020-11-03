import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("symbol-processing") version "1.4.10-dev-experimental-20201023"
}

version = "0.0.1"

dependencies {
    implementation(kotlin("reflect"))

    implementation(project(":internal-processor"))
    ksp(project(":internal-processor"))

    testImplementation("io.kotest:kotest-runner-junit5:4.3.1")
    testImplementation("io.kotest:kotest-assertions-core:4.3.1")
    testImplementation("io.kotest:kotest-property:4.3.1")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/source/main/kotlin/")
        }
    }
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

ksp {
    arg("pkg", "de.f0x.lokales")
    arg("upTo", "10")
}