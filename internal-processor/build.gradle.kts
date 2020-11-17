import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `common-build`
    kotlin("jvm")
}

version = "0.0.1"

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.10-dev-experimental-20201023")
    implementation("com.squareup:kotlinpoet:1.7.2")

    testImplementation("io.kotest:kotest-runner-junit5:4.3.1")
    testImplementation("io.kotest:kotest-assertions-core:4.3.1")
    testImplementation("io.kotest:kotest-property:4.3.1")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.3.1")
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