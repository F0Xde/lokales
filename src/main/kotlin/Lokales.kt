package de.f0x.lokales

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.reflect

class LokaleBundle(
    val locale: Locale,
    private val translations: Map<String, Translation>
) {
    operator fun get(key: String, vararg args: Any?) =
        translations[key]?.get(args)
}

class LokaleBundleBuilder(val locale: Locale) {
    val translations: MutableMap<String, Translation> = HashMap()

    infix fun String.to(value: String) {
        translations[this] = ValTranslation(value)
    }

    inline infix fun <reified A> String.to(noinline fn: (String) -> String) {
        // Invoking the function manually has to be done because `KFunction.call` is not yet implemented
        translations[this] = FnTranslation(fn.withTypes(A::class)) { fn(it[0]) }
    }

    inline infix fun <reified A, reified B> String.to(
        noinline fn: (String, String) -> String
    ) {
        translations[this] = FnTranslation(fn.withTypes(A::class, B::class)) {
            fn(it[0], it[1])
        }
    }

    inline infix fun <reified A, reified B, reified C> String.to(
        noinline fn: (String, String, String) -> String
    ) {
        translations[this] = FnTranslation(fn.withTypes(A::class, B::class, C::class)) {
            fn(it[0], it[1], it[2])
        }
    }

    inline infix fun <reified A, reified B, reified C, reified D> String.to(
        noinline fn: (String, String, String, String) -> String
    ) {
        translations[this] = FnTranslation(
            fn.withTypes(A::class, B::class, C::class, D::class)
        ) {
            fn(it[0], it[1], it[2], it[3])
        }
    }

    inline infix fun <reified A, reified B, reified C, reified D, reified E> String.to(
        noinline fn: (String, String, String, String, String) -> String
    ) {
        translations[this] = FnTranslation(
            fn.withTypes(A::class, B::class, C::class, D::class, E::class)
        ) {
            fn(it[0], it[1], it[2], it[3], it[4])
        }
    }

    fun build() = LokaleBundle(locale, translations)
}

fun lokale(locale: Locale, init: LokaleBundleBuilder.() -> Unit) =
    LokaleBundleBuilder(locale).apply(init).build()

interface Translation {
    operator fun get(vararg args: Any?): String
}

class ValTranslation(private val value: String) : Translation {
    override fun get(vararg args: Any?) = value
}

class FnTranslation(
    private val args: List<Pair<String, KClass<*>>>,
    private val fn: (List<String>) -> String
) : Translation {
    override fun get(vararg args: Any?): String {
        lokale(Locale.US) {
            "test" to "abc"
            "test1".to<String> { name -> "Welcome, $name" }
        }
        if (args.size != this.args.size) {
            throw IllegalArgumentException("Wrong number of arguments passed to Translation")
        }
        val stringArgs = args.zip(this.args).map { (arg, expected) ->
            val (name, type) = expected
            if (type.isInstance(arg)) {
                // TODO special conversion for numbers, dates, etc.
                arg.toString()
            } else {
                throw IllegalArgumentException("Argument $name is not of type $type")
            }
        }
        return fn(stringArgs)
    }
}

val Function<*>.argNames: List<String>
    get() = reflect()!!.valueParameters.mapNotNull { it.name }

fun Function<*>.withTypes(vararg types: KClass<*>) = argNames.zip(types)