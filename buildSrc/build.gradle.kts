plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin:1.4.10"))
    implementation("com.github.ben-manes:gradle-versions-plugin:0.36.0")
}