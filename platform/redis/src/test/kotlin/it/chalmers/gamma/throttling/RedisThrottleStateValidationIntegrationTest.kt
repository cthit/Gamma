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
import kotlin.test.assertTrue

class RedisThrottleStateValidationIntegrationTest {
    @Test
    fun `reservation rejects malformed generation before creating marker or changing counter`() =
        withRedisThrottling { throttling, redis ->
            val key = ThrottleKey("malformed-reservation-generation")
            val counter = redis.key(RedisArea.THROTTLE, key.value)
            val generation = redis.key(RedisArea.THROTTLE_GENERATION, key.value)
            redis.set(counter, "2", Duration.ofMinutes(1))
            redis.set(generation, "bad|generation", Duration.ofMinutes(1))

            assertFailsWith<RedisUnavailable> { throttling.reserve(reservationRequest(key)) }

            assertEquals("2", redis.get(counter))
            assertEquals("bad|generation", redis.get(generation))
            assertEquals(0, reservationMarkerCount(redis))
        }

    @Test
    fun `refund rejects malformed marker grammar without deleting marker or changing counter`() =
        withRedisThrottling { throttling, redis ->
            val malformedMarkers =
                listOf(
                    "|1|$FIRST_GENERATION",
                    "1||$FIRST_GENERATION",
                    "1|$FIRST_GENERATION|",
                    "1|$FIRST_GENERATION|$SECOND_GENERATION",
                    "1|not-a-generation",
                    "1|${FIRST_GENERATION.uppercase()}",
                )

            malformedMarkers.forEachIndexed { index, marker ->
                val key = ThrottleKey("malformed-marker-$index")
                val reservation = checkNotNull(throttling.reserve(reservationRequest(key)))
                replaceOnlyReservationMarker(redis, marker)

                assertFailsWith<RedisUnavailable>("marker=$marker") { throttling.refund(reservation) }

                assertEquals("1", redis.get(redis.key(RedisArea.THROTTLE, key.value)))
                assertEquals(marker, onlyReservationMarker(redis))
                deleteReservationMarkers(redis)
                throttling.delete(key)
            }
        }

    @Test
    fun `refund rejects generation whose expiry no longer covers its counter`() =
        withRedisThrottling { throttling, redis ->
            val key = ThrottleKey("short-refund-generation")
            val reservation = checkNotNull(throttling.reserve(reservationRequest(key)))
            val generationKey = redis.key(RedisArea.THROTTLE_GENERATION, key.value)
            val issuedGeneration = checkNotNull(redis.get(generationKey))
            redis.set(generationKey, issuedGeneration, Duration.ofSeconds(30))

            assertFailsWith<RedisUnavailable> { throttling.refund(reservation) }

            assertEquals("1", redis.get(redis.key(RedisArea.THROTTLE, key.value)))
            assertEquals(1, reservationMarkerCount(redis))
        }

    private fun reservationMarkerCount(redis: GammaRedis): Long =
        redis.evaluateLong(
            "return #redis.call('KEYS', ARGV[1])",
            emptyList(),
            listOf(RESERVATION_MARKER_PATTERN),
        )

    private fun replaceOnlyReservationMarker(
        redis: GammaRedis,
        marker: String,
    ) {
        check(
            redis.evaluateString(
                """
                local keys = redis.call('KEYS', ARGV[1])
                if #keys ~= 1 then return 'unexpected-marker-count' end
                redis.call('SET', keys[1], ARGV[2], 'KEEPTTL')
                return redis.call('GET', keys[1])
                """.trimIndent(),
                emptyList(),
                listOf(RESERVATION_MARKER_PATTERN, marker),
            ) == marker,
        )
    }

    private fun onlyReservationMarker(redis: GammaRedis): String? =
        redis.evaluateString(
            """
            local keys = redis.call('KEYS', ARGV[1])
            if #keys ~= 1 then return nil end
            return redis.call('GET', keys[1])
            """.trimIndent(),
            emptyList(),
            listOf(RESERVATION_MARKER_PATTERN),
        )

    private fun deleteReservationMarkers(redis: GammaRedis) {
        redis.evaluateLong(
            """
            local keys = redis.call('KEYS', ARGV[1])
            if #keys == 0 then return 0 end
            return redis.call('UNLINK', unpack(keys))
            """.trimIndent(),
            emptyList(),
            listOf(RESERVATION_MARKER_PATTERN),
        )
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

    private fun withRedisThrottling(test: (RedisThrottling, GammaRedis) -> Unit) {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                run { test(RedisThrottling(redis), redis) }
            }
        }
    }

    private companion object {
        const val FIRST_GENERATION = "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa"
        const val SECOND_GENERATION = "bbbbbbbb-bbbb-4bbb-9bbb-bbbbbbbbbbbb"
        const val RESERVATION_MARKER_PATTERN = "gamma:kotlin:v1:throttle-reservation:*"
    }
}
