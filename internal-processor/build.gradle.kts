plugins {
    `common-build`
}

version = "0.0.1"

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.20-dev-experimental-20210120")
    implementation("com.squareup:kotlinpoet:1.7.2")

    kotest()
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.3.5")
}