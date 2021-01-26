package de.f0x.lokales

import java.util.*

class TranslationBundle(
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

fun lokale(locale: Locale, init: TranslationBundleBuilder.() -> Unit) =
    TranslationBundleBuilder(locale).apply(init).build()