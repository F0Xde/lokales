plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin:1.4.21"))
    implementation("com.github.ben-manes:gradle-versions-plugin:0.36.0")
}