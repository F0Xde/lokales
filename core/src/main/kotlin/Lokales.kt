package de.f0x.lokales

import java.util.*
import kotlin.collections.HashMap

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class Lokale(val name: String, vararg val languageTags: String)

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
        translations[this] = ValTranslation(value)
    }

    fun build() = LokaleBundle(locale, translations)
}

fun lokale(locale: Locale, init: LokaleBundleBuilder.() -> Unit) =
    LokaleBundleBuilder(locale).apply(init).build()