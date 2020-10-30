package de.f0x.lokales

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.reflect

class LokaleBundle internal constructor(
    val locale: Locale,
    private val translations: Map<String, Translation>
) {
    operator fun get(key: String, vararg args: Any?) =
        translations[key]?.get(args)
}

class LokaleBundleBuilder(val locale: Locale) {
    private val translations: MutableMap<String, Translation> = HashMap()

    infix fun String.to(value: String) {
        translations[this] = ValTranslation(value)
    }

    infix fun <A> String.to(fn: (String) -> String) {
        // Invoking the function manually has to be done because `KFunction.call` is not yet implemented
        translations[this] = FnTranslation(fn.namedArgs) { fn(it[0]) }
    }

    infix fun <A, B> String.to(fn: (String, String) -> String) {
        translations[this] = FnTranslation(fn.namedArgs) { fn(it[0], it[1]) }
    }

    infix fun <A, B, C> String.to(fn: (String, String, String) -> String) {
        translations[this] = FnTranslation(fn.namedArgs) { fn(it[0], it[1], it[2]) }
    }

    infix fun <A, B, C, D> String.to(fn: (String, String, String, String) -> String) {
        translations[this] = FnTranslation(fn.namedArgs) { fn(it[0], it[1], it[2], it[3]) }
    }

    infix fun <A, B, C, D, E> String.to(fn: (String, String, String, String, String) -> String) {
        translations[this] = FnTranslation(fn.namedArgs) { fn(it[0], it[1], it[2], it[3], it[4]) }
    }

    fun build() = LokaleBundle(locale, translations)

    private val Function<*>.namedArgs: List<Pair<String, KType>>
        get() = reflect()!!.valueParameters.map {
            it.name!! to it.type
        }
}

fun lokale(locale: Locale, init: LokaleBundleBuilder.() -> Unit) =
    LokaleBundleBuilder(locale).apply(init).build()

internal interface Translation {
    operator fun get(vararg args: Any?): String
}

internal class ValTranslation(private val value: String) : Translation {
    override fun get(vararg args: Any?) = value
}

internal class FnTranslation(
    private val args: List<Pair<String, KType>>,
    private val fn: (List<String>) -> String
) : Translation {
    override fun get(vararg args: Any?): String {
        if (args.size != this.args.size) {
            throw IllegalArgumentException("Wrong number of arguments passed to Translation")
        }
        val stringArgs = args.zip(this.args).map { (arg, expected) ->
            val (name, type) = expected
            if ((arg == null && type.isMarkedNullable) ||
                (arg != null && type.isSupertypeOf(arg::class.starProjectedType))
            ) {
                // TODO special conversion for numbers, dates, etc.
                arg.toString()
            } else {
                throw IllegalArgumentException("Argument $name is not of type $type")
            }
        }
        return fn(stringArgs)
    }
}