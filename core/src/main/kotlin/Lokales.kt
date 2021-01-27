package de.f0x.lokales

import java.util.*

class TranslationBundle(
    val locale: Locale,
    private val translations: Map<String, Translation>
) {
    operator fun get(key: String): String =
        translations[key]?.get(*arrayOfNulls(0)) ?: throw MissingTranslationException(key, locale)

    operator fun get(key: String, vararg args: Any?): String =
        translations[key]?.get(*args) ?: throw MissingTranslationException(key, locale)

    operator fun get(key: String, vararg args: Pair<String, Any?>): String =
        translations[key]?.get(*args) ?: throw MissingTranslationException(key, locale)
}

class MissingTranslationException(val key: String, val locale: Locale) :
    Exception("Translation key '$key' not present in bundle for $locale")

fun lokale(locale: Locale, init: TranslationBundleBuilder.() -> Unit) =
    TranslationBundleBuilder(locale).apply(init).build()