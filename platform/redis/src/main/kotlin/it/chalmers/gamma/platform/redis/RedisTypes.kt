package it.chalmers.gamma.platform.redis

import java.time.Instant

enum class RedisArea(
    internal val segment: String,
) {
    SESSION("session"),
    SESSION_REVOCATION("session-revocation"),
    LOGIN_COMPLETION("login-completion"),
    API_TOKEN_VERIFICATION("api-token-verification"),
    THROTTLE("throttle"),
    THROTTLE_GENERATION("throttle-generation"),
    THROTTLE_RESERVATION("throttle-reservation"),
    OAUTH_AUTHORIZATION("oauth-authorization"),
    OAUTH_INDEX("oauth-index"),
}

class GammaRedisKey internal constructor(
    internal val storageKey: String,
) {
    override fun toString(): String = "GammaRedisKey(<redacted>)"
}

internal class RedisKeyspace {
    fun key(
        area: RedisArea,
        identifier: String,
    ): GammaRedisKey = GammaRedisKey(prefix(area) + identifier)

    fun prefix(area: RedisArea): String = "$KEY_NAMESPACE:${area.segment}:"

    companion object {
        const val KEY_NAMESPACE = "gamma:kotlin:v1"
    }
}

data class RedisEntry(
    val identifier: String,
    val payload: String,
    val timeToLiveMillis: Long,
) {
    override fun toString(): String =
        "RedisEntry(identifier=<redacted>, payload=<redacted>, timeToLiveMillis=$timeToLiveMillis)"
}

class RedisEntrySnapshot internal constructor(
    entries: List<RedisEntry>,
    val complete: Boolean,
    val observedAt: Instant,
) {
    val entries: List<RedisEntry> = entries.toList()

    override fun toString(): String = "RedisEntrySnapshot(entries=${entries.size}, complete=$complete)"
}

class RedisUnavailable(
    message: String,
) : RuntimeException(message)
