plugins {
    `common-build`
}

version = "0.0.1"

dependencies {
    implementation(project(":core"))

    implementation(kotlin("script-runtime"))
    implementation(kotlin("scripting-compiler-embeddable"))
    implementation(kotlin("script-util"))
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata:1.7.2")

    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.10-dev-experimental-20201023")

    kotest()
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.3.1")
}