package it.chalmers.gamma.apiaccess

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ApiAccessSecurityValuesTest {
    @Test
    fun `stored credentials and raw tokens redact their string representations`() {
        val stored = StoredApiCredential("{bcrypt}sensitive-hash")
        val raw = RawApiToken("sensitive-raw-api-token-value-long-enough")

        assertNotEquals(stored.value, stored.toString())
        assertNotEquals(raw.value, raw.toString())
        assertEquals("<value redacted>", stored.toString())
        assertEquals("<value redacted>", raw.toString())
    }

    @Test
    fun `disabled verification cache always misses and accepts remember as a no-op`() =
        run {
            val id = ApiKeyId(UUID.fromString("59000000-0000-0000-0000-000000000001"))
            val stored = StoredApiCredential("{bcrypt}stored")
            val token = RawApiToken("presented-api-token-value-that-is-long-enough")

            assertEquals(CachedApiTokenMatch.MISS, ApiTokenVerificationCache.Disabled.match(id, stored, token))
            ApiTokenVerificationCache.Disabled.remember(id, stored, token)
            assertEquals(CachedApiTokenMatch.MISS, ApiTokenVerificationCache.Disabled.match(id, stored, token))
        }
}
