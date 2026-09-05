package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.GammaRedisKey
import it.chalmers.gamma.platform.redis.RedisArea
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.time.Clock
import java.time.Duration
import java.util.UUID

/** Atomic Redis persistence for Spring Authorization Server state and its digest lookup indexes. */
class RedisOAuthAuthorizationStore(
    private val redis: GammaRedis,
    private val clock: Clock = Clock.systemUTC(),
) {
    internal fun save(
        desiredAuthorization: StoredOAuthAuthorization,
        expectedRevision: String?,
    ): String {
        val recordKey = recordKey(desiredAuthorization.id)
        val currentPayload = redis.get(recordKey)
        val current = currentPayload?.let { decode(it, desiredAuthorization.id) }
        if (current?.revision != expectedRevision || (current == null && expectedRevision != null)) {
            throw OAuthAuthorizationConflict()
        }

        val authorization = desiredAuthorization.copy(revision = UUID.randomUUID().toString())
        authorization.validate(clock)
        val expiry = Duration.between(clock.instant(), authorization.expiresAt)
        require(!expiry.isNegative && !expiry.isZero) { "OAuth authorization is already expired" }
        if (current != null) {
            check(current.registeredClientId == authorization.registeredClientId) {
                "OAuth authorization client cannot change"
            }
            check(current.principalName == authorization.principalName) {
                "OAuth authorization principal cannot change"
            }
        }

        val oldIndexes = current?.indexes.orEmpty().map(::indexKey)
        val newIndexes = authorization.indexes.map(::indexKey)
        val result =
            redis.evaluateLong(
                OAuthAuthorizationRedisScripts.SAVE,
                listOf(recordKey) + oldIndexes + newIndexes,
                listOf(
                    currentPayload ?: ABSENT_RECORD,
                    JSON.encodeToString(authorization),
                    expiry.toMillis().coerceAtLeast(1).toString(),
                    authorization.id,
                    oldIndexes.size.toString(),
                    newIndexes.size.toString(),
                ),
            )
        when (result) {
            SAVE_SUCCEEDED -> {
                return authorization.revision
            }

            SAVE_RETRY -> {
                throw OAuthAuthorizationConflict()
            }

            SAVE_INDEX_CONFLICT -> {
                throw OAuthAuthorizationStorageFailure("OAuth authorization lookup index is inconsistent")
            }

            else -> {
                throw OAuthAuthorizationStorageFailure("OAuth authorization storage failed")
            }
        }
    }

    internal fun findById(id: String): StoredOAuthAuthorization? {
        val payload = redis.get(recordKey(id)) ?: return null
        val authorization = decode(payload, id)
        if (!authorization.expiresAt.isAfter(clock.instant())) {
            removeByIdIfRevision(id, authorization.revision)
            return null
        }
        return authorization
    }

    internal fun findByIndex(
        kind: OAuthAuthorizationIndexKind,
        sensitiveValue: String,
        stateClientId: String? = null,
    ): StoredOAuthAuthorization? {
        val indexedValue =
            if (kind == OAuthAuthorizationIndexKind.STATE) {
                val clientId = stateClientId ?: return null
                "$clientId\u0000$sensitiveValue"
            } else {
                sensitiveValue
            }
        val reference = StoredOAuthAuthorizationIndex(kind, GammaRedis.digest(indexedValue))
        val authorizationId = redis.get(indexKey(reference)) ?: return null
        val authorization = findById(authorizationId) ?: return null
        return authorization.takeIf { reference in it.indexes }
    }

    internal fun removeById(id: String) {
        removeById(id, requiredRevision = null)
    }

    internal fun removeByIdIfRevision(
        id: String,
        requiredRevision: String,
    ): Boolean = removeById(id, requiredRevision)

    private fun removeById(
        id: String,
        requiredRevision: String?,
    ): Boolean {
        repeat(MAXIMUM_COMPARE_AND_SET_ATTEMPTS) {
            val recordKey = recordKey(id)
            val currentPayload = redis.get(recordKey)
            if (currentPayload == null) return requiredRevision == null
            val current = decode(currentPayload, id)
            if (requiredRevision != null && current.revision != requiredRevision) return false
            val result =
                redis.evaluateLong(
                    OAuthAuthorizationRedisScripts.REMOVE,
                    listOf(recordKey) + current.indexes.map(::indexKey),
                    listOf(currentPayload, id),
                )
            if (result == REMOVE_SUCCEEDED) return true
            if (result != REMOVE_RETRY) {
                throw OAuthAuthorizationStorageFailure("OAuth authorization removal failed")
            }
        }
        throw OAuthAuthorizationStorageFailure("OAuth authorization changed during removal")
    }

    private fun decode(
        payload: String,
        expectedId: String,
    ): StoredOAuthAuthorization =
        try {
            JSON.decodeFromString<StoredOAuthAuthorization>(payload).also { authorization ->
                authorization.validate(clock)
                check(authorization.id == expectedId) { "OAuth authorization id is inconsistent" }
            }
        } catch (_: SerializationException) {
            throw OAuthAuthorizationStorageFailure("OAuth authorization state is invalid")
        } catch (_: IllegalArgumentException) {
            throw OAuthAuthorizationStorageFailure("OAuth authorization state is invalid")
        } catch (_: IllegalStateException) {
            throw OAuthAuthorizationStorageFailure("OAuth authorization state is invalid")
        }

    private fun recordKey(id: String): GammaRedisKey =
        redis.key(RedisArea.OAUTH_AUTHORIZATION, "record:${GammaRedis.digest(id)}")

    private fun indexKey(index: StoredOAuthAuthorizationIndex): GammaRedisKey =
        redis.key(RedisArea.OAUTH_INDEX, "${index.kind.storageName}:${index.digest}")

    private companion object {
        val JSON = Json { ignoreUnknownKeys = false }
        const val ABSENT_RECORD = "<absent>"
        const val MAXIMUM_COMPARE_AND_SET_ATTEMPTS = 5
        const val SAVE_SUCCEEDED = 1L
        const val SAVE_RETRY = 0L
        const val SAVE_INDEX_CONFLICT = -1L
        const val REMOVE_SUCCEEDED = 1L
        const val REMOVE_RETRY = 0L
    }
}

internal open class OAuthAuthorizationStorageFailure(
    message: String,
) : RuntimeException(message)

internal class OAuthAuthorizationConflict : OAuthAuthorizationStorageFailure("OAuth authorization changed concurrently")
