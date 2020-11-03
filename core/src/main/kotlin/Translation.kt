package de.f0x.lokales

interface Translation {
    operator fun get(vararg args: Any?): String

    operator fun get(vararg args: Pair<String, Any?>): String
}

class ValTranslation(private val value: String) : Translation {
    override fun get(vararg args: Any?): String = value

    override fun get(vararg args: Pair<String, Any?>) = value
}