pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "symbol-processing") {
                useModule("com.google.devtools.ksp:symbol-processing:${requested.version}")
            }
        }
    }

    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "lokales"
include("core", "processor", "internal-processor")