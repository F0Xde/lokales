import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.kotest() {
    val version = "4.3.2"
    add("testImplementation", "io.kotest:kotest-runner-junit5:$version")
    add("testImplementation", "io.kotest:kotest-assertions-core:$version")
    add("testImplementation", "io.kotest:kotest-property:$version")
}