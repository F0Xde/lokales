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

    private val formatterType = ClassName("de.f0x.lokales", "LokaleFormatter")

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
            (1..upTo).forEach {
                addType(generateTranslationClass(it))
                addFunction(generateBuilderInvokeFun(pkg, it))
                addFunction(generateFixedBuilderFun(pkg, "str", List(it) { String::class.asTypeName() }))
                addFunction(generateFixedBuilderFun(pkg, "any", List(it) {
                    Any::class.asTypeName().copy(nullable = true)
                }))
            }
        }.build()

    private fun generateTranslationClass(params: Int): TypeSpec {
        val stringType = String::class.asTypeName()
        val anyNullable = Any::class.asTypeName().copy(nullable = true)

        val typeVariables = createTypeVariables(params)
        val fnType = formatterLambda(typeVariables)

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
                            (0 until params).zip(typeVariables).joinToString {
                                "args[${it.first}] as ${it.second.name}"
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
            .addParameter("fn", formatterLambda(typeVariables))
            .addStatement(
                "return %T(this.formatter, fn)",
                ClassName(pkg, "FnTranslation$params").parameterizedBy(typeVariables)
            )
            .build()
    }

    private fun generateFixedBuilderFun(pkg: String, name: String, paramTypes: List<TypeName>): FunSpec =
        FunSpec.builder(name)
            .addModifiers(KModifier.INFIX)
            .receiver(String::class)
            .addParameter("fn", formatterLambda(paramTypes))
            .returns(String::class)
            .addStatement(
                "return %T(this.formatter, fn)",
                ClassName(pkg, "FnTranslation${paramTypes.size}").parameterizedBy(paramTypes)
            )
            .build()

    private fun formatterLambda(params: List<TypeName>) =
        LambdaTypeName.get(
            formatterType,
            parameters = params.toTypedArray(),
            String::class.asTypeName()
        )

    private fun createTypeVariables(params: Int): List<TypeVariableName> =
        List(params) { TypeVariableName(('A' + it).toString()) }
}