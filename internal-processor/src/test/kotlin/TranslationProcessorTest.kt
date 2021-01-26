import com.tschuchort.compiletesting.*
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import de.f0x.lokales.internal.processor.TranslationProcessor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.file.shouldBeADirectory
import io.kotest.matchers.file.shouldNotBeEmptyDirectory
import io.kotest.matchers.shouldBe

class TranslationProcessorTest : StringSpec({
    "processor should generate file" {
        val dummySource = SourceFile.kotlin(
            "TestFile.kt", """
        package de.f0x.lokales.test
        """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(dummySource)

            symbolProcessors = listOf(TranslationProcessor())
            kspArgs["pkg"] = "de.f0x.lokales.test"
            kspArgs["upTo"] = "10"
        }
        val result = compilation.compile()
        result.exitCode shouldBe ExitCode.OK
        compilation.kspSourcesDir.shouldBeADirectory()
        compilation.kspSourcesDir.shouldNotBeEmptyDirectory()
    }
})