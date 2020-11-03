package de.f0x.lokales.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import de.f0x.lokales.Lokale
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.script.ScriptEngineManager
import javax.tools.Diagnostic.Kind

class LokaleProcessor : SymbolProcessor {
    lateinit var codeGenerator: CodeGenerator

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator
    }

    override fun process(resolver: Resolver) {
    }

    override fun finish() {
    }

    inner class X {

    }
}

@AutoService(Processor::class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class LokaleProcessorKapt : AbstractProcessor() {
    private val messager: Messager
        get() = processingEnv.messager

    private val outputDir: String
        get() = processingEnv.options["kapt.kotlin.generated"]!!

    override fun getSupportedAnnotationTypes() =
        setOf(Lokale::class.java.canonicalName)

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val engine = ScriptEngineManager().getEngineByExtension("kts")

        messager.printMessage(Kind.WARNING, roundEnv.getElementsAnnotatedWith(Lokale::class.java).toString())
        roundEnv.getElementsAnnotatedWith(Lokale::class.java).forEach {
            val annotation = it.getAnnotation(Lokale::class.java)
            val locales = annotation.languageTags.mapNotNull { tag ->
                try {
                    Locale.Builder().setLanguageTag(tag).build()
                } catch (e: IllformedLocaleException) {
                    messager.printMessage(Kind.ERROR, "Not a valid language tag", it)
                    null
                }
            }
            println(it.enclosingElement)
        }

        return false
    }

    fun generateBundle(pkg: String, name: String, vararg locales: Locale) {
    }
}