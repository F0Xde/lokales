package de.f0x.lokales

interface Translation {
    operator fun get(vararg args: Any?): String

    operator fun get(args: Map<String, Any?>): String

    operator fun get(vararg args: Pair<String, Any?>) = get(args.toMap())
}

class ValTranslation(private val value: String) : Translation {
    override fun get(vararg args: Any?): String = value

    override fun get(args: Map<String, Any?>) = value
}