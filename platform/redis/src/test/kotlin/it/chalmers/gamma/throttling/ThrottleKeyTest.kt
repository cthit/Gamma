package it.chalmers.gamma.throttling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ThrottleKeyTest {
    @Test
    fun `digest accepts arbitrary valid identity characters without exposing them`() {
        val email = "michael+alerts%gamma@example.org"
        val key = ThrottleKey.digest("password-reset", email)

        assertEquals(
            "password-reset:oHCxScXQuELDPcnXJFX3RB0P56cgj8L0JzJ63mtTIHc",
            key.value,
        )
        assertTrue(email !in key.value)
        assertTrue(email !in key.toString())
        assertTrue(key.value !in key.toString())
        assertEquals(key, ThrottleKey.digest("password-reset", email))
        assertNotEquals(key, ThrottleKey.digest("password-reset", "other@example.org"))
    }

    @Test
    fun `literal throttle keys enforce storage safe characters and length`() {
        assertEquals("login:127.0.0.1", ThrottleKey("login:127.0.0.1").value)
        listOf("", "contains space", "slash/value", "x".repeat(201)).forEach { value ->
            assertFailsWith<IllegalArgumentException>(value) { ThrottleKey(value) }
        }
    }

    @Test
    fun `digest namespaces reject separators and invalid lengths`() {
        listOf("", "login:account", "space here", "x".repeat(81)).forEach { namespace ->
            assertFailsWith<IllegalArgumentException>(namespace) { ThrottleKey.digest(namespace, "value") }
        }
    }
}
