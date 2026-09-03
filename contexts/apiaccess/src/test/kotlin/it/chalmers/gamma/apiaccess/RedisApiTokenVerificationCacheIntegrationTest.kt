package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.RedisArea
import it.chalmers.gamma.platform.redis.RedisSettings
import it.chalmers.gamma.testing.RedisTestEnvironment
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RedisApiTokenVerificationCacheIntegrationTest {
    @Test
    fun `cache representation and expiration remain compatible`() {
        RedisTestEnvironment().use { environment ->
            GammaRedis(RedisSettings(environment.host, environment.port)).use { redis ->
                val signingKey = "fixed-api-token-cache-signing-key".toByteArray()
                val otherSigningKey = "other-api-token-cache-signing-key".toByteArray()
                val id = ApiKeyId(UUID.fromString("57000000-0000-0000-0000-000000000001"))
                val credential = StoredApiCredential("{bcrypt}\$2y\$10\$fixed-stored-credential-value")
                val otherCredential = StoredApiCredential("{bcrypt}\$2y\$10\$different-stored-credential")
                val token = RawApiToken("fixed-presented-api-token-value-000001")
                val otherToken = RawApiToken("different-presented-api-token-000002")
                val cache = redisApiTokenVerificationCache(redis, signingKey)

                val cacheKeyHmac = hmac(signingKey, "gamma-api-token-verification-key")
                val tokenHmac = hmac(signingKey, "gamma-api-token-verification-token")
                val identifier = "${id.value}:${encodedHmac(cacheKeyHmac, credential.value)}"
                val expectedKey = redis.key(RedisArea.API_TOKEN_VERIFICATION, identifier)
                val expectedStorageKey = "gamma:kotlin:v1:api-token-verification:$identifier"
                val expectedPayload = encodedHmac(tokenHmac, token.value)

                run {
                    assertEquals(CachedApiTokenMatch.MISS, cache.match(id, credential, token))

                    cache.remember(id, credential, token)

                    assertEquals(expectedPayload, redis.get(expectedKey))
                    assertEquals(
                        expectedStorageKey,
                        redis.evaluateString("return KEYS[1]", listOf(expectedKey), emptyList()),
                    )
                    val ttl =
                        redis.evaluateLong(
                            "return redis.call('PTTL', KEYS[1])",
                            listOf(expectedKey),
                            emptyList(),
                        )
                    assertTrue(ttl in 86_390_000L..86_400_000L, "Unexpected cache TTL: $ttl")
                    assertEquals(CachedApiTokenMatch.MATCH, cache.match(id, credential, token))
                    assertEquals(CachedApiTokenMatch.MISMATCH, cache.match(id, credential, otherToken))
                    assertEquals(CachedApiTokenMatch.MISS, cache.match(id, otherCredential, token))
                    assertEquals(
                        CachedApiTokenMatch.MISS,
                        redisApiTokenVerificationCache(redis, otherSigningKey).match(id, credential, token),
                    )
                }
            }
        }
    }
}

private fun hmac(
    key: ByteArray,
    value: String,
): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return mac.doFinal(value.toByteArray(Charsets.UTF_8))
}

private fun encodedHmac(
    key: ByteArray,
    value: String,
): String = Base64.getUrlEncoder().withoutPadding().encodeToString(hmac(key, value))
