package it.chalmers.gamma

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.RedisArea
import it.chalmers.gamma.platform.redis.RedisSettings
import it.chalmers.gamma.platform.redis.RedisUnavailable
import it.chalmers.gamma.testing.RedisTestEnvironment
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration
import java.util.function.Supplier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RedisLifecycleIntegrationTest {
    @Test
    fun `application pause and stop preserve usable Redis state after resuming the same adapter`() {
        RedisTestEnvironment().use { server ->
            AnnotationConfigApplicationContext().use { context ->
                context.registerBean(
                    "redisConnectionFactory",
                    LettuceConnectionFactory::class.java,
                    Supplier { LettuceConnectionFactory(RedisStandaloneConfiguration(server.host, server.port)) },
                )
                context.registerBean(
                    "gammaRedis",
                    GammaRedis::class.java,
                    Supplier { GammaRedis(context.getBean(LettuceConnectionFactory::class.java)) },
                )
                context.registerBean(
                    "redisLifecycle",
                    RedisLifecycle::class.java,
                    Supplier { RedisLifecycle(context.getBean(GammaRedis::class.java)) },
                )
                context.refresh()
                val redis = context.getBean(GammaRedis::class.java)
                val key = redis.key(RedisArea.SESSION, "lifecycle")
                redis.set(key, "before pause", Duration.ofMinutes(1))
                repeat(2) {
                    if (it == 0) context.pause() else context.stop()
                    assertFalse(redis.isRunning())
                    assertFalse(redis.isClosed)
                    assertFailsWith<RedisUnavailable> { redis.ping() }
                    if (it == 0) context.restart() else context.start()
                    assertTrue(redis.isRunning())
                    assertTrue(redis.ping())
                    assertEquals(if (it == 0) "before pause" else "after resume", redis.get(key))
                    redis.set(key, "after resume", Duration.ofMinutes(1))
                }
            }
        }
    }

    @Test
    fun `standalone lifecycle resumes but final close is permanent`() {
        RedisTestEnvironment().use { server ->
            val redis = GammaRedis(RedisSettings(server.host, server.port))
            redis.use {
                assertTrue(redis.isRunning())
                redis.stop()
                redis.stop()
                assertFalse(redis.isRunning())
                assertFalse(redis.isClosed)
                redis.start()
                redis.start()
                assertTrue(redis.ping())
            }
            assertTrue(redis.isClosed)
            assertFalse(redis.isRunning())
            assertFailsWith<IllegalStateException> { redis.start() }
            assertFailsWith<RedisUnavailable> { redis.ping() }
        }
    }
}
