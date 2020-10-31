# Lokales

Define localizations directly in your Kotlin code.

## Usage

Create a `LokaleBundle` using the builder function:
```kotlin
import de.f0x.lokales.lokale

val usBundle = lokale(Locale.US) {
    "key" to "value"
    "anotherKey" { argument -> "Received $argument" }
    // ...
}
```
Retrieve translations with the `get` operator functions:
```kotlin
assert(usBundle["key"] == "value")

// Positional arguments
assert(usBundle["anotherKey", 23] == "Received 23")

// Named arguments
assert(usBundle["anotherKey", "argument" to 23] == "Received 23")
```
Named and positional arguments cannot be mixed, choose one.

## Roadmap

- Generate bundle classes for type safe translations

## License

This project is made available under the Apache 2.0 license.
See `LICENSE` for the full license agreement.