plugins {
    kotlin("jvm") version "1.4.10" apply false
    id("com.github.ben-manes.versions") version "0.33.0"
}

allprojects {
    group = "de.f0x.lokales"

    repositories {
        jcenter()
        google()
    }
}