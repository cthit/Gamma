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

class RedisThrottleSnapshotValidationIntegrationTest {
    @Test
    fun `snapshot rejects every noncanonical counter representation without disclosure`() =
        withRedisThrottling { throttling, redis ->
            val invalidPayloads = listOf("+1", " 1", "1 ", "01", "0", "9223372036854775808", "1:1")

            invalidPayloads.forEachIndexed { index, payload ->
                val key = ThrottleKey("invalid-payload-$index")
                val counter = redis.key(RedisArea.THROTTLE, key.value)
                redis.set(counter, payload, Duration.ofMinutes(1))

                val failure = assertFailsWith<RedisUnavailable> { throttling.snapshot() }

                assertEquals("Redis throttle state is invalid", failure.message)
                assertFalse(failure.toString().contains(key.value))
                redis.delete(counter)
            }
        }

    @Test
    fun `snapshot normalizes an invalid stored identifier to redacted Redis state failure`() =
        withRedisThrottling { throttling, redis ->
            val invalidStorageKey = "gamma:kotlin:v1:throttle:contains space"
            redis.evaluateString(
                "return redis.call('SET', ARGV[1], ARGV[2], 'PX', ARGV[3])",
                emptyList(),
                listOf(invalidStorageKey, "1", Duration.ofMinutes(1).toMillis().toString()),
            )

            val failure = assertFailsWith<RedisUnavailable> { throttling.snapshot() }

            assertEquals("Redis throttle state is invalid", failure.message)
            assertFalse(failure.toString().contains(invalidStorageKey))
        }

    private fun withRedisThrottling(test: (RedisThrottling, GammaRedis) -> Unit) {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                run { test(RedisThrottling(redis), redis) }
            }
        }
    }
}
