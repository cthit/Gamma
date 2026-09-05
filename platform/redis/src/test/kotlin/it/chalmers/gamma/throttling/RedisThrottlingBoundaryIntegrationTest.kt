package it.chalmers.gamma.throttling

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.RedisArea
import it.chalmers.gamma.platform.redis.RedisSettings
import it.chalmers.gamma.platform.redis.RedisUnavailable
import it.chalmers.gamma.testing.RedisTestEnvironment
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RedisThrottlingBoundaryIntegrationTest {
    @Test
    fun `refund cannot alter a counter recreated after administrative deletion`() {
        withThrottling { throttling ->
            val key = ThrottleKey("deleted-reservation-counter")
            val request = reservationRequest(key)
            val original = checkNotNull(throttling.reserve(request))

            throttling.delete(key)
            val replacement = checkNotNull(throttling.reserve(request))
            throttling.refund(original)

            assertEquals(
                1L,
                throttling
                    .snapshot()
                    .entries
                    .single()
                    .attempts,
            )

            throttling.refund(replacement)
            assertTrue(throttling.snapshot().entries.isEmpty())
        }
    }

    @Test
    fun `Redis rejects reservations it did not issue`() {
        withThrottling { throttling ->
            val foreignReservation = object : ThrottleReservation() {}

            assertFailsWith<IllegalArgumentException> { throttling.refund(foreignReservation) }
            assertFailsWith<IllegalArgumentException> { throttling.commit(foreignReservation) }
        }
    }

    @Test
    fun `administrative snapshots disclose their scan bound`() {
        withThrottling { throttling ->
            repeat(3) { index ->
                assertTrue(throttling.charge(ThrottleKey("snapshot-$index"), 10, Duration.ofMinutes(1)))
            }

            val bounded = throttling.snapshot(ThrottleEntryLimit(2))
            val complete = throttling.snapshot(ThrottleEntryLimit(3))

            assertEquals(2, bounded.entries.size)
            assertFalse(bounded.complete)
            assertEquals(3, complete.entries.size)
            assertTrue(complete.complete)
        }
    }

    @Test
    fun `malformed counter state fails closed`() {
        withRedisThrottling { throttling, redis ->
            val chargeKey = ThrottleKey("malformed-charge")
            setCounter(redis, chargeKey, "0")
            assertFailsWith<RedisUnavailable> {
                throttling.charge(chargeKey, maximumAttempts = 10, window = Duration.ofMinutes(1))
            }
            throttling.delete(chargeKey)

            val reserveKey = ThrottleKey("malformed-reserve")
            setCounter(redis, reserveKey, "0")
            assertFailsWith<RedisUnavailable> { throttling.reserve(reservationRequest(reserveKey)) }
            throttling.delete(reserveKey)

            val refundKey = ThrottleKey("malformed-refund")
            val reservation = checkNotNull(throttling.reserve(reservationRequest(refundKey)))
            setCounter(redis, refundKey, "0")
            assertFailsWith<RedisUnavailable> { throttling.refund(reservation) }

            setCounter(redis, refundKey, "1")
            alignGenerationWithCounter(redis, refundKey)
            throttling.refund(reservation)
            assertTrue(throttling.snapshot().entries.none { it.key == refundKey })

            val oversizedKey = ThrottleKey("oversized-count")
            setCounter(redis, oversizedKey, "2147483648")
            assertFailsWith<RedisUnavailable> {
                throttling.charge(oversizedKey, maximumAttempts = 10, window = Duration.ofMinutes(1))
            }
        }
    }

    @Test
    fun `unsupported windows are rejected before a multi limit reservation reaches Redis`() {
        withThrottling { throttling ->
            val supported =
                ThrottleLimit(
                    ThrottleKey("supported-window"),
                    maximumAttempts = 10,
                    window = Duration.ofMinutes(1),
                    charge = ThrottleCharge.FAILED_ATTEMPT,
                )

            assertFailsWith<IllegalArgumentException> {
                ThrottleReservationRequest(
                    listOf(
                        supported,
                        ThrottleLimit(
                            ThrottleKey("unsupported-window"),
                            maximumAttempts = 10,
                            window = Duration.ofMillis(Long.MAX_VALUE),
                            charge = ThrottleCharge.FAILED_ATTEMPT,
                        ),
                    ),
                )
            }
            assertTrue(throttling.snapshot().entries.isEmpty())
        }
    }

    @Test
    fun `fixed window charges saturate at the supported integer range`() {
        withRedisThrottling { throttling, redis ->
            val key = ThrottleKey("saturated-counter")
            setCounter(redis, key, Int.MAX_VALUE.toString())

            assertFalse(
                throttling.charge(
                    key,
                    maximumAttempts = Int.MAX_VALUE,
                    window = Duration.ofMinutes(1),
                ),
            )
            assertFalse(
                throttling.charge(
                    key,
                    maximumAttempts = Int.MAX_VALUE,
                    window = Duration.ofMinutes(1),
                ),
            )
            assertEquals(
                Int.MAX_VALUE.toLong(),
                throttling
                    .snapshot()
                    .entries
                    .single()
                    .attempts,
            )
        }
    }

    private fun reservationRequest(key: ThrottleKey) =
        ThrottleReservationRequest(
            listOf(
                ThrottleLimit(
                    key,
                    maximumAttempts = 10,
                    window = Duration.ofMinutes(1),
                    charge = ThrottleCharge.FAILED_ATTEMPT,
                ),
            ),
        )

    private fun setCounter(
        redis: GammaRedis,
        key: ThrottleKey,
        payload: String,
    ) {
        redis.set(redis.key(RedisArea.THROTTLE, key.value), payload, Duration.ofMinutes(1))
    }

    private fun alignGenerationWithCounter(
        redis: GammaRedis,
        key: ThrottleKey,
    ) {
        val generationKey = redis.key(RedisArea.THROTTLE_GENERATION, key.value)
        redis.set(generationKey, checkNotNull(redis.get(generationKey)), Duration.ofMinutes(1))
    }

    private fun withThrottling(test: (RedisThrottling) -> Unit) {
        withRedisThrottling { throttling, _ -> test(throttling) }
    }

    private fun withRedisThrottling(test: (RedisThrottling, GammaRedis) -> Unit) {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                run { test(RedisThrottling(redis), redis) }
            }
        }
    }
}
