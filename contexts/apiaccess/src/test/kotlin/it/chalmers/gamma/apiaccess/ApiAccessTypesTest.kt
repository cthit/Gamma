package it.chalmers.gamma.apiaccess

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class ApiAccessTypesTest {
    @Test
    fun `api key names accept the persisted boundaries`() {
        assertEquals("ab", ApiKeyName("ab").value)
        assertEquals("a".repeat(30), ApiKeyName("a".repeat(30)).value)
    }

    @Test
    fun `api key names reject invalid lengths and unsafe markup`() {
        listOf("a", "a".repeat(31), "unsafe<script>", "quote\"").forEach { value ->
            assertFailsWith<IllegalArgumentException>(value) { ApiKeyName(value) }
        }
    }

    @Test
    fun `api tokens enforce entropy length and redact diagnostics`() {
        val token = RawApiToken("t".repeat(32))

        assertEquals(32, token.value.length)
        assertNotEquals(token.value, token.toString())
        assertFailsWith<IllegalArgumentException> { RawApiToken("t".repeat(31)) }
        assertFailsWith<IllegalArgumentException> { RawApiToken("t".repeat(101)) }
    }

    @Test
    fun `api key ids round trip through their external representation`() {
        val raw = "11111111-1111-4111-8111-111111111111"

        assertEquals(UUID.fromString(raw), ApiKeyId.parse(raw).value)
        assertFailsWith<IllegalArgumentException> { ApiKeyId.parse("not-a-uuid") }
    }
}
