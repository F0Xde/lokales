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
            "greeting" to { name -> "Welcome, $name!" }
            "goodbye" to { name -> "See  you later $name!" }
        }
        bundle["greeting", "name" to "F0X"] shouldBe "Welcome, F0X!"
        bundle["goodbye", "name" to "F0X"] shouldBe "See you later F0X!"
    }
})