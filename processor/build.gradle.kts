import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `common-build`
    kotlin("kapt")
}

version = "0.0.1"

repositories {
    google()
}

dependencies {
    implementation(project(":core"))

    implementation(kotlin("script-runtime"))
    implementation(kotlin("scripting-compiler-embeddable"))
    implementation(kotlin("script-util"))
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata:1.7.2")

    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.10-dev-experimental-20201023")

    implementation("com.google.auto.service:auto-service:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")

    testImplementation("io.kotest:kotest-runner-junit5:4.3.1")
    testImplementation("io.kotest:kotest-assertions-core:4.3.1")
    testImplementation("io.kotest:kotest-property:4.3.1")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.3.1")
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