package it.chalmers.gamma.throttling

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.GammaRedisKey
import it.chalmers.gamma.platform.redis.RedisArea
import it.chalmers.gamma.platform.redis.RedisUnavailable
import java.time.Duration
import java.time.Instant
import java.util.UUID

class RedisThrottling(
    private val redis: GammaRedis,
) : ThrottleReservations,
    FixedWindowThrottling,
    ThrottleEntryStore {
    override fun reserve(request: ThrottleReservationRequest): ThrottleReservation? {
        val limits = request.limits
        val reservation =
            RedisThrottleReservation(
                UUID.randomUUID(),
                limits.filter { it.charge == ThrottleCharge.FAILED_ATTEMPT }.map { it.key },
            )
        val proposedGenerations = limits.map { UUID.randomUUID().toString() }
        val result =
            redis.evaluateLong(
                RedisThrottleScripts.RESERVE,
                listOf(redis.key(RedisArea.THROTTLE_RESERVATION, reservation.id.toString())) +
                    limits.flatMap { limit -> counterKeys(limit.key).asList() },
                buildList {
                    limits.forEachIndexed { index, limit ->
                        add(limit.maximumAttempts.toString())
                        add(limit.window.toMillis().toString())
                        add(proposedGenerations[index])
                        add((limit.charge == ThrottleCharge.FAILED_ATTEMPT).toRedisFlag())
                    }
                    add(RESERVATION_TIME_TO_LIVE.toMillis().toString())
                },
            )
        return when (result) {
            1L -> reservation
            0L -> null
            else -> throw RedisUnavailable(INVALID_THROTTLE_STATE)
        }
    }

    override fun refund(reservation: ThrottleReservation) =
        completeSecurityOperation {
            val issuedReservation = requireIssuedReservation(reservation)
            val result =
                redis.evaluateLong(
                    RedisThrottleScripts.REFUND,
                    listOf(redis.key(RedisArea.THROTTLE_RESERVATION, issuedReservation.id.toString())) +
                        issuedReservation.refundableKeys.flatMap { key -> counterKeys(key).asList() },
                    emptyList(),
                )
            if (result !in 0L..1L) throw RedisUnavailable(INVALID_THROTTLE_STATE)
        }

    override fun commit(reservation: ThrottleReservation) =
        completeSecurityOperation {
            val issuedReservation = requireIssuedReservation(reservation)
            val result =
                redis.evaluateLong(
                    RedisThrottleScripts.COMPLETE_RESERVATION,
                    listOf(redis.key(RedisArea.THROTTLE_RESERVATION, issuedReservation.id.toString())),
                    emptyList(),
                )
            if (result !in 0L..1L) throw RedisUnavailable(INVALID_THROTTLE_STATE)
        }

    override fun charge(
        key: ThrottleKey,
        maximumAttempts: Int,
        window: Duration,
    ): Boolean {
        val limit = FixedWindowLimit(maximumAttempts, window)
        val attempts =
            redis.evaluateLong(
                RedisThrottleScripts.INCREMENT_COUNTER,
                counterKeys(key).asList(),
                listOf(limit.window.toMillis().toString(), UUID.randomUUID().toString()),
            )
        if (attempts < 1) throw RedisUnavailable(INVALID_THROTTLE_STATE)
        return attempts <= limit.maximumAttempts
    }

    override fun snapshot(limit: ThrottleEntryLimit): ThrottleEntrySnapshot {
        val storedSnapshot = redis.scanEntries(RedisArea.THROTTLE, limit.value)
        val entries =
            storedSnapshot.entries
                .map { stored ->
                    ThrottleEntry(
                        key = decodeThrottleKey(stored.identifier),
                        attempts = decodeAttempts(stored.payload),
                        expiresAt = decodeExpiration(storedSnapshot.observedAt, stored.timeToLiveMillis),
                    )
                }.sortedBy { it.key.value }
        return ThrottleEntrySnapshot(entries, storedSnapshot.complete)
    }

    override fun delete(key: ThrottleKey) {
        val counter = counterKeys(key)
        redis.delete(counter.value, counter.generation)
    }

    private fun requireIssuedReservation(reservation: ThrottleReservation): RedisThrottleReservation =
        reservation as? RedisThrottleReservation
            ?: throw IllegalArgumentException("Throttle reservation was not issued by Redis throttling")

    private fun counterKeys(key: ThrottleKey): RedisThrottleCounter =
        RedisThrottleCounter(
            value = redis.key(RedisArea.THROTTLE, key.value),
            generation = redis.key(RedisArea.THROTTLE_GENERATION, key.value),
        )

    private fun decodeThrottleKey(identifier: String): ThrottleKey =
        try {
            ThrottleKey(identifier)
        } catch (_: IllegalArgumentException) {
            throw RedisUnavailable(INVALID_THROTTLE_STATE)
        }

    private fun decodeAttempts(payload: String): Long =
        payload
            .takeIf(CANONICAL_COUNTER_PATTERN::matches)
            ?.toLongOrNull()
            ?.takeIf { it > 0 }
            ?: throw RedisUnavailable(INVALID_THROTTLE_STATE)

    private fun decodeExpiration(
        observedAt: Instant,
        timeToLiveMillis: Long,
    ): Instant {
        if (timeToLiveMillis <= 0) throw RedisUnavailable(INVALID_THROTTLE_STATE)
        return observedAt.plusMillis(timeToLiveMillis)
    }

    private companion object {
        val CANONICAL_COUNTER_PATTERN = Regex("^[1-9][0-9]*$")
        const val INVALID_THROTTLE_STATE = "Redis throttle state is invalid"
        val RESERVATION_TIME_TO_LIVE: Duration = Duration.ofMinutes(2)
    }
}

private class RedisThrottleReservation(
    val id: UUID,
    refundableKeys: List<ThrottleKey>,
) : ThrottleReservation() {
    val refundableKeys: List<ThrottleKey> = refundableKeys.toList()

    override fun toString(): String = "RedisThrottleReservation(id=<redacted>, refundableKeys=${refundableKeys.size})"
}

private data class RedisThrottleCounter(
    val value: GammaRedisKey,
    val generation: GammaRedisKey,
) {
    fun asList(): List<GammaRedisKey> = listOf(value, generation)
}

private fun Boolean.toRedisFlag(): String = if (this) "1" else "0"
