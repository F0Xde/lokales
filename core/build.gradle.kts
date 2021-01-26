plugins {
    `common-build`
    id("com.google.devtools.ksp") version "1.4.20-dev-experimental-20210120"
}

version = "0.0.1"

dependencies {
    implementation(kotlin("reflect"))

    implementation(project(":internal-processor"))
    ksp(project(":internal-processor"))

    kotest()
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
        test {
            kotlin.srcDir("build/generated/ksp/test/kotlin")
        }
    }
}

ksp {
    arg("pkg", "de.f0x.lokales")
    arg("upTo", "10")
}