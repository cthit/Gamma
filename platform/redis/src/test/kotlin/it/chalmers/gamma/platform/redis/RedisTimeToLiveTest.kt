package it.chalmers.gamma.platform.redis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RedisTimeToLiveTest {
    @Test
    fun `TTL is unchanged when observation is immediate`() {
        assertEquals(100, ageRedisTimeToLive(100, measuredAtNanos = 7, observedAtNanos = 7))
    }

    @Test
    fun `partial milliseconds are rounded up so expiry is never overstated`() {
        assertEquals(
            99,
            ageRedisTimeToLive(
                timeToLiveMillis = 100,
                measuredAtNanos = 1_000_000,
                observedAtNanos = 1_000_001,
            ),
        )
        assertEquals(
            98,
            ageRedisTimeToLive(
                timeToLiveMillis = 100,
                measuredAtNanos = 1_000_000,
                observedAtNanos = 2_000_001,
            ),
        )
    }

    @Test
    fun `elapsed whole milliseconds are subtracted exactly`() {
        assertEquals(
            97,
            ageRedisTimeToLive(
                timeToLiveMillis = 100,
                measuredAtNanos = 2_000_000,
                observedAtNanos = 5_000_000,
            ),
        )
    }

    @Test
    fun `elapsed time remains correct when the monotonic clock wraps`() {
        assertEquals(
            99,
            ageRedisTimeToLive(
                timeToLiveMillis = 100,
                measuredAtNanos = Long.MAX_VALUE,
                observedAtNanos = Long.MIN_VALUE,
            ),
        )
    }

    @Test
    fun `expired observations are represented as non-positive TTLs`() {
        assertEquals(
            0,
            ageRedisTimeToLive(
                timeToLiveMillis = 2,
                measuredAtNanos = 0,
                observedAtNanos = 2_000_000,
            ),
        )
    }

    @Test
    fun `observation cannot precede measurement`() {
        assertFailsWith<IllegalArgumentException> {
            ageRedisTimeToLive(
                timeToLiveMillis = 100,
                measuredAtNanos = 2,
                observedAtNanos = 1,
            )
        }
    }
}
