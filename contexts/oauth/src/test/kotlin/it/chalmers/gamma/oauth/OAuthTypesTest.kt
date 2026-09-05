package it.chalmers.gamma.oauth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class OAuthTypesTest {
    @Test
    fun `redirect uris preserve the released non-empty sanitized string contract`() {
        listOf(
            "https://example.org/callback",
            "http://example.org/callback",
            "http://localhost:8080/callback",
            "https://example.org/callback#fragment",
            "/relative/callback",
            "https:///callback",
            "https://user@example.org/callback",
        ).forEach { value -> assertEquals(value, RedirectUri(value).value) }

        assertFailsWith<IllegalArgumentException> { RedirectUri("") }
        listOf('&', '<', '>', '"', '\'').forEach { unsafe ->
            assertFailsWith<IllegalArgumentException> { RedirectUri("https://example.org/$unsafe") }
        }
    }

    @Test
    fun `client identifiers reject ambiguous formats`() {
        assertEquals(30, ClientId("A234567890A234567890A234567890").value.length)
        assertFailsWith<IllegalArgumentException> { ClientId("lowercase-client-id") }
    }

    @Test
    fun `client secrets and api tokens enforce entropy and redact diagnostics`() {
        val secret = RawClientSecret("s".repeat(32))
        val token = OAuthApiToken("t".repeat(32))

        assertNotEquals(secret.value, secret.toString())
        assertNotEquals(token.value, token.toString())
        assertFailsWith<IllegalArgumentException> { RawClientSecret("s".repeat(31)) }
        assertFailsWith<IllegalArgumentException> { OAuthApiToken("t".repeat(31)) }
        assertFailsWith<IllegalArgumentException> { RawClientSecret("s".repeat(101)) }
        assertFailsWith<IllegalArgumentException> { OAuthApiToken("t".repeat(101)) }
    }

    @Test
    fun `authority names preserve the 2_5_1 persisted vocabulary`() {
        assertEquals("ab", AuthorityName("ab").value)
        assertEquals("calendarreadown2", AuthorityName("calendarreadown2").value)
        assertEquals(30, AuthorityName("a".repeat(30)).value.length)
        listOf(
            "a",
            "a".repeat(31),
            "space here",
            "calendar.read:own",
            "UPPERCASE",
            "unsafe<script>",
        ).forEach { value ->
            assertFailsWith<IllegalArgumentException>(value) { AuthorityName(value) }
        }
    }

    @Test
    fun `oauth identifiers round trip UUID values`() {
        val raw = "11111111-1111-4111-8111-111111111111"

        assertEquals(raw, ClientUid.parse(raw).value.toString())
        assertEquals(raw, OAuthApiKeyId.parse(raw).value.toString())
    }

    @Test
    fun `client names enforce the persisted length and html safety boundaries`() {
        assertEquals("a".repeat(30), ClientName("a".repeat(30)).value)
        assertFailsWith<IllegalArgumentException> { ClientName("a") }
        assertFailsWith<IllegalArgumentException> { ClientName("a".repeat(31)) }
        assertFailsWith<IllegalArgumentException> { ClientName("unsafe<script>") }
    }
}
