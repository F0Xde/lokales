package de.f0x.lokales

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.reflect

class LokaleBundle(
    val locale: Locale,
    private val translations: Map<String, Translation>
) {
    operator fun get(key: String) =
        translations[key]?.get(*arrayOfNulls(0))

    operator fun get(key: String, vararg args: Any?) =
        translations[key]?.get(*args)

    operator fun get(key: String, vararg args: Pair<String, Any?>) =
        translations[key]?.get(*args)
}

class LokaleBundleBuilder(val locale: Locale) {
    private val translations: MutableMap<String, Translation> = HashMap()

    infix fun String.to(value: String) {
        translations[this] = ValTranslation(locale, value)
    }

    infix fun String.to(fn: (String) -> String) =
        trans(fn) { fn(it[0]) }

    infix fun String.to(fn: (String, String) -> String) =
        trans(fn) { fn(it[0], it[1]) }

    infix fun String.to(fn: (String, String, String) -> String) =
        trans(fn) { fn(it[0], it[1], it[2]) }

    infix fun String.to(fn: (String, String, String, String) -> String) =
        trans(fn) { fn(it[0], it[1], it[2], it[3]) }

    infix fun String.to(fn: (String, String, String, String, String) -> String) =
        trans(fn) { fn(it[0], it[1], it[2], it[3], it[4]) }

    private fun String.trans(fn: Function<String>, listFn: (List<String>) -> String) {
        translations[this] =
            FnTranslation(locale, fn.reflect()!!.valueParameters.map { it.name!! }, listFn)
    }

    fun build() = LokaleBundle(locale, translations)
}

fun lokale(locale: Locale, init: LokaleBundleBuilder.() -> Unit) =
    LokaleBundleBuilder(locale).apply(init).build()

sealed class Translation(val locale: Locale) {
    abstract operator fun get(vararg args: Any?): String

    abstract operator fun get(vararg args: Pair<String, Any?>): String

    fun localize(value: Any?): String {
        // TODO special conversion for numbers, dates etc.
        return value.toString()
    }
}

class ValTranslation(locale: Locale, private val value: String) : Translation(locale) {
    override fun get(vararg args: Any?): String = value

    override fun get(vararg args: Pair<String, Any?>) = value
}

class FnTranslation(
    locale: Locale,
    private val argNames: List<String>,
    private val fn: (List<String>) -> String
) : Translation(locale) {
    override fun get(vararg args: Any?): String {
        if (args.size != argNames.size) {
            throw IllegalArgumentException("Wrong number of arguments, expected $argNames")
        }
        return fn(args.map { it.toString() })
    }

    override fun get(vararg args: Pair<String, Any?>): String {
        val stringArgs = argNames.map {
            args.first { (name, _) -> name == it }.second.toString()
        }
        return fn(stringArgs)
    }
}