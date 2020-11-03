import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import de.f0x.lokales.processor.LokaleProcessorKapt
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class LokaleProcessorTest : StringSpec({
    "Lokale annotation should error on invalid language tag " {
        val source = SourceFile.kotlin(
            "TestLokale.kt", """
            @file:Lokale("so_cool_lol", "fakelanguagetaglol123")
            
            import de.f0x.lokales.Lokale
            
            fun main() {
            
            }
        """
        )
        val result = KotlinCompilation().apply {
            sources = listOf(source)
            annotationProcessors = listOf(LokaleProcessorKapt())

            inheritClassPath = true
        }.compile()

        result.exitCode shouldBe ExitCode.COMPILATION_ERROR
    }
})