# Lokales

Define localizations directly in your Kotlin code.

## Usage

Create a `TranslationBundle` using the builder function (up to 10 arguments are possible):

```kotlin
de.f0x.lokales.lokale

val usBundle = lokale(Locale.US) {
    "key" to "value"
    "anotherKey" str { "$it has to be a string" }
    "key3" any { "$it can be anything" }
    "lastKey" { typed: SomeType -> "Specify own types" }
    // ...
}
```

Retrieve translations with the `get` operator functions:

```kotlin
assert(usBundle["key"] == "value")

// Positional arguments
assert(usBundle["anotherKey", "This"] == "This has to be a string")

// Named arguments
assert(usBundle["key3", "argument" to 23] == "23 can be anything")
```

Named and positional arguments cannot be mixed, choose one.

## Roadmap

- Generate bundle classes for type safe translations

## License

This project is made available under the Apache 2.0 license. See `LICENSE` for the full license agreement.