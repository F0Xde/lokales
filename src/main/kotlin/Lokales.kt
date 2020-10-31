package de.f0x.lokales

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KFunction
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.jvm.reflect

class LokaleBundle(
    val locale: Locale,
    private val translations: Map<String, Translation>
) {
    operator fun get(key: String, vararg args: Pair<String, Any?>) =
        translations[key]?.get(*args)
}

class LokaleBundleBuilder(val locale: Locale) {
    private val translations: MutableMap<String, Translation> = HashMap()

    infix fun String.to(value: String) {
        translations[this] = ValTranslation(value)
    }

    infix fun String.to(fn: (String) -> String) = trans(fn)

    infix fun String.to(fn: (String, String) -> String) = trans(fn)

    infix fun String.to(fn: (String, String, String) -> String) = trans(fn)

    infix fun String.to(fn: (String, String, String, String) -> String) = trans(fn)

    infix fun String.to(fn: (String, String, String, String, String) -> String) = trans(fn)

    private fun String.trans(fn: Function<String>) {
        translations[this] =
            FnTranslation(fn.reflect() ?: throw RuntimeException("Failed to inspect given function $fn"))
    }

    fun build() = LokaleBundle(locale, translations)
}

fun lokale(locale: Locale, init: LokaleBundleBuilder.() -> Unit) =
    LokaleBundleBuilder(locale).apply(init).build()

interface Translation {
    operator fun get(vararg args: Pair<String, Any?>): String
}

class ValTranslation(private val value: String) : Translation {
    override fun get(vararg args: Pair<String, Any?>) = value
}

class FnTranslation(private val fn: KFunction<String>) : Translation {
    override fun get(vararg args: Pair<String, Any?>): String {
        return fn.callBy(args.map { (name, value) ->
            // TODO special conversion for numbers, dates etc.
            fn.findParameterByName(name)!! to value.toString()
        }.toMap())
    }
}