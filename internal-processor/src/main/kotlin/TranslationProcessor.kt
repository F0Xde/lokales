package de.f0x.lokales.internal.processor

import com.google.devtools.ksp.processing.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class TranslationProcessor : SymbolProcessor {
    private lateinit var codeGenerator: CodeGenerator
    private lateinit var logger: KSPLogger

    private lateinit var pkg: String
    private var upTo by Delegates.notNull<Int>()

    private val contextType = ClassName("de.f0x.lokales", "TranslationContext")
    private val translationType = ClassName("de.f0x.lokales", "Translation")
    private val valTranslationType = ClassName("de.f0x.lokales", "ValTranslation")
    private val translationContextType = ClassName("de.f0x.lokales", "TranslationContext")
    private val translationBundleType = ClassName("de.f0x.lokales", "TranslationBundle")

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
            .createNewFile(
                Dependencies(true),
                fileSpec.packageName,
                fileSpec.name.substringBefore('.'),
                "kt"
            )
            .bufferedWriter()
            .use {
                fileSpec.writeTo(it)
            }
    }

    override fun finish() {
    }

    private fun generateTranslations(pkg: String, upTo: Int): FileSpec =
        FileSpec.builder(pkg, "Translations.kt").apply {
            val builderClass = beginBundleBuilderClass()
            (1..upTo).forEach {
                addType(generateTranslationClass(it))
                builderClass.addFunction(generateBuilderInvokeFun(pkg, it))
                builderClass.addFunction(generateFixedBuilderFun(pkg, "str", List(it) { String::class.asTypeName() }))
                builderClass.addFunction(generateFixedBuilderFun(pkg, "any", List(it) {
                    Any::class.asTypeName().copy(nullable = true)
                }))
            }
            addType(builderClass.build())
        }.build()

    private fun beginBundleBuilderClass(): TypeSpec.Builder =
        TypeSpec.classBuilder("TranslationBundleBuilder")
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("locale", Locale::class)
                    .build()
            ).addProperty(
                PropertySpec.builder("locale", Locale::class)
                    .initializer("locale")
                    .build()
            ).addProperty(
                PropertySpec.builder("context", translationContextType, KModifier.PRIVATE)
                    .initializer(CodeBlock.of("%T(locale)", translationContextType))
                    .build()
            ).addProperty(
                PropertySpec.builder(
                    "translations",
                    MUTABLE_MAP.parameterizedBy(String::class.asTypeName(), translationType),
                    KModifier.PRIVATE
                )
                    .initializer(CodeBlock.of("%T()", HashMap::class))
                    .build()
            ).addFunction(
                FunSpec.builder("build")
                    .returns(translationBundleType)
                    .addStatement("return %T(locale, translations)", translationBundleType)
                    .build()
            ).addFunction(
                FunSpec.builder("to")
                    .addModifiers(KModifier.INFIX)
                    .receiver(String::class)
                    .addParameter("value", String::class)
                    .addStatement("translations[this] = %T(value)", valTranslationType)
                    .build()
            )

    private fun generateTranslationClass(params: Int): TypeSpec {
        val stringType = String::class.asTypeName()
        val anyNullable = Any::class.asTypeName().copy(nullable = true)

        val typeVariables = createTypeVariables(params)
        val fnType = contextLambda(typeVariables)

        return TypeSpec.classBuilder("FnTranslation$params")
            .addTypeVariables(typeVariables.map { TypeVariableName(it.name, KModifier.IN) })
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("context", contextType, KModifier.PRIVATE)
                    .addParameter("fn", fnType)
                    .build()
            ).addProperty(
                PropertySpec.builder("context", contextType)
                    .initializer("context")
                    .build()
            ).addProperty(
                PropertySpec.builder("fn", fnType, KModifier.PRIVATE)
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
                        "return context.fn(${
                            (0 until params).zip(typeVariables).joinToString { (i, type) ->
                                "args[$i] as ${type.name}"
                            }
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

    private fun generateBuilderInvokeFun(pkg: String, params: Int): FunSpec {
        val typeVariables = createTypeVariables(params)
        return FunSpec.builder("invoke")
            .addModifiers(KModifier.OPERATOR)
            .addTypeVariables(typeVariables)
            .receiver(String::class)
            .addParameter("fn", contextLambda(typeVariables))
            .addStatement(
                "translations[this] = %T(context, fn)",
                ClassName(pkg, "FnTranslation$params").parameterizedBy(typeVariables)
            )
            .build()
    }

    private fun generateFixedBuilderFun(pkg: String, name: String, paramTypes: List<TypeName>): FunSpec =
        FunSpec.builder(name)
            .addModifiers(KModifier.INFIX)
            .receiver(String::class)
            .addParameter("fn", contextLambda(paramTypes))
            .addStatement(
                "translations[this] = %T(context, fn)",
                ClassName(pkg, "FnTranslation${paramTypes.size}").parameterizedBy(paramTypes)
            )
            .build()

    private fun contextLambda(params: List<TypeName>) =
        LambdaTypeName.get(
            contextType,
            parameters = params.toTypedArray(),
            String::class.asTypeName()
        )

    private fun createTypeVariables(params: Int): List<TypeVariableName> =
        List(params) { TypeVariableName(('A' + it).toString()) }
}