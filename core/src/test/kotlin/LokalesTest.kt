import de.f0x.lokales.MissingTranslationException
import de.f0x.lokales.lokale
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class LokalesTest : StringSpec({
    "lokale should translate value" {
        val bundle = lokale(Locale.US) {
            "key" to "value"
            "key.second" to "another cool value"
        }
        bundle["key"] shouldBe "value"
        bundle["key.second"] shouldBe "another cool value"
    }

    "lokale should translate function" {
        val bundle = lokale(Locale.US) {
            "greeting" str { name -> "Welcome, $name!" }
            "goodbye" str { name -> "See you later $name!" }
        }
        bundle["greeting", "name" to "Donald"] shouldBe "Welcome, Donald!"
        bundle["goodbye", "Donald D."] shouldBe "See you later Donald D.!"
    }

    "lokale should maintain argument order" {
        val bundle = lokale(Locale.US) {
            "greeting" any { name, age, size, color, male ->
                "Welcome $name with age $age, you are $size cm tall and like $color (male: $male)!"
            }
        }

        val name = "Peter1"
        val age = 19
        val size = 187
        val color = "blue"
        val male = true
        bundle["greeting", "age" to age, "color" to color, "name" to name, "male" to male, "size" to size] shouldBe
                "Welcome $name with age $age, you are $size cm tall and like $color (male: $male)!"
        bundle["greeting", name, age, size, color, male] shouldBe
                "Welcome $name with age $age, you are $size cm tall and like $color (male: $male)!"
    }

    "lokale should throw on invalid key" {
        val bundle = lokale(Locale.US) {
            "key" to "value"
        }
        shouldNotThrowAny { bundle["key"] }
        with(shouldThrow<MissingTranslationException> { bundle["invalidKey"] }) {
            key shouldBe "invalidKey"
            locale shouldBe Locale.US
        }
    }
})