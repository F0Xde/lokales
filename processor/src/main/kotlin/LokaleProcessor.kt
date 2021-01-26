package de.f0x.lokales.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor

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
}