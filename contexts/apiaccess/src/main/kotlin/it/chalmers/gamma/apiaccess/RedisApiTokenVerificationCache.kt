package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.RedisArea
import java.security.MessageDigest
import java.time.Duration
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun redisApiTokenVerificationCache(
    redis: GammaRedis,
    signingKey: ByteArray,
): ApiTokenVerificationCache = RedisApiTokenVerificationCache(redis, signingKey)

private class RedisApiTokenVerificationCache(
    private val redis: GammaRedis,
    signingKey: ByteArray,
) : ApiTokenVerificationCache {
    private val cacheKey = hmac(signingKey, "gamma-api-token-verification-key")
    private val tokenKey = hmac(signingKey, "gamma-api-token-verification-token")

    override fun match(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        presentedToken: RawApiToken,
    ): CachedApiTokenMatch {
        val expected = redis.get(redisKey(id, storedCredential)) ?: return CachedApiTokenMatch.MISS
        val presented = encodedHmac(tokenKey, presentedToken.value)
        return if (MessageDigest.isEqual(expected.toByteArray(), presented.toByteArray())) {
            CachedApiTokenMatch.MATCH
        } else {
            CachedApiTokenMatch.MISMATCH
        }
    }

    override fun remember(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    ) {
        redis.set(redisKey(id, storedCredential), encodedHmac(tokenKey, token.value), Duration.ofHours(24))
    }

    private fun redisKey(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
    ) = redis.key(
        RedisArea.API_TOKEN_VERIFICATION,
        "${id.value}:${encodedHmac(cacheKey, storedCredential.value)}",
    )

    private companion object {
        fun hmac(
            key: ByteArray,
            value: String,
        ): ByteArray {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(key, "HmacSHA256"))
            return mac.doFinal(value.toByteArray(Charsets.UTF_8))
        }

        fun encodedHmac(
            key: ByteArray,
            value: String,
        ): String = Base64.getUrlEncoder().withoutPadding().encodeToString(hmac(key, value))
    }
}
