import de.f0x.lokales.lokale
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class LokalesTest : StringSpec({
    "lokale should translate value" {
        val bundle = lokale(Locale.US) {
            "key" to "value"
            "key.second" to "another COOL value"
        }
        bundle["key"] shouldBe "value"
        bundle["key.second"] shouldBe "another COOL value"
    }

    "lokale should translate function" {
        val bundle = lokale(Locale.US) {
            "greeting" { name -> "Welcome, $name!" }
            "goodbye" { name -> "See you later $name!" }
        }
        bundle["greeting", "name" to "Donald"] shouldBe "Welcome, Donald!"
        bundle["goodbye", "Donald D"] shouldBe "See you later Donald D!"
    }

    "lokale should maintain argument order" {
        val name = "Peter"
        val age = 19
        val size = 187
        val color = "blue"
        val male = true
        val bundle = lokale(Locale.US) {
            "greeting" { name, age, size, color, male ->
                "Welcome $name with age $age, you are $size cm tall and like $color (gender: $male)!"
            }
        }
        bundle["greeting", "age" to age, "color" to color, "name" to name, "male" to male, "size" to size] shouldBe
                "Welcome $name with age $age, you are $size cm tall and like $color (gender: $male)!"
        bundle["greeting", name, age, size, color, male] shouldBe
                "Welcome $name with age $age, you are $size cm tall and like $color (gender: $male)!"
    }
})