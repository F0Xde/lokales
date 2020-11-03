package de.f0x.lokales.internal.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.properties.Delegates

class TranslationProcessor : SymbolProcessor {
    private lateinit var codeGenerator: CodeGenerator
    private lateinit var logger: KSPLogger

    private lateinit var pkg: String
    private var upTo by Delegates.notNull<Int>()

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator
        this.logger = logger
        this.pkg = options["pkg"] ?: error("Required argument 'pkg' missing")
        this.upTo = options["upTo"]?.toInt() ?: error("Required argument 'upTo' missing")
    }

    override fun process(resolver: Resolver) {
        val fileSpec = generateTranslations(pkg, upTo)
        codeGenerator
            .createNewFile(fileSpec.packageName, fileSpec.name.substringBefore('.'), "kt")
            .bufferedWriter()
            .use {
                fileSpec.writeTo(it)
            }
    }

    override fun finish() {
    }

    private fun generateTranslations(pkg: String, upTo: Int): FileSpec =
        FileSpec.builder(pkg, "FnTranslations.kt").apply {
            (1..upTo).forEach { addType(generateTranslationClass(it)) }
        }.build()

    private fun generateTranslationClass(params: Int): TypeSpec {
        val stringType = String::class.asTypeName()
        val anyNullable = Any::class.asTypeName().copy(nullable = true)

        val typeVariables = (0 until params).map { TypeVariableName(('A' + it).toString()) }
        val formatterType = ClassName("de.f0x.lokales", "LokaleFormatter")
        val fnType = LambdaTypeName.get(
            formatterType,
            parameters = typeVariables.toTypedArray(),
            stringType
        )

        return TypeSpec.classBuilder("FnTranslation$params")
            .addTypeVariables(typeVariables.map { TypeVariableName(it.name, KModifier.IN) })
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("formatter", formatterType)
                    .addParameter("fn", fnType)
                    .build()
            ).addProperty(
                PropertySpec.builder("formatter", formatterType)
                    .initializer("formatter")
                    .build()
            ).addProperty(
                PropertySpec.builder("fn", fnType)
                    .initializer("fn")
                    .build()
            ).addSuperinterface(ClassName("de.f0x.lokales", "Translation"))
            .addFunction(
                FunSpec.builder("get")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("args", anyNullable, KModifier.VARARG)
                    .returns(stringType)
                    .beginControlFlow("if (args.size != $params)")
                    .addStatement("throw IllegalArgumentException(\"Wrong number of arguments, expected $params got \${args.size}\")")
                    .endControlFlow()
                    .addStatement("@Suppress(\"UNCHECKED_CAST\")")
                    .addStatement(
                        "return formatter.fn(${
                            (0 until params).zip(typeVariables).map {
                                "args[${it.first}] as ${it.second.name}"
                            }.joinToString()
                        })"
                    )
                    .build()
            ).addFunction(
                FunSpec.builder("get")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(
                        "args",
                        Pair::class.asTypeName().parameterizedBy(stringType, anyNullable),
                        KModifier.VARARG
                    ).returns(stringType)
                    .addStatement("TODO()")
                    .build()
            )
            .build()
    }
}