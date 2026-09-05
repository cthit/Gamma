package it.chalmers.gamma.platform.redis

import it.chalmers.gamma.testing.RedisTestEnvironment
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GammaRedisIntegrationTest {
    @Test
    @Suppress("MissingUseCall") // This test closes the resource twice to exercise idempotence.
    fun `close state is observable and closing twice is harmless`() {
        RedisTestEnvironment().use { redisServer ->
            val redis = GammaRedis(RedisSettings(redisServer.host, redisServer.port))

            assertFalse(redis.isClosed)
            redis.close()
            assertTrue(redis.isClosed)
            redis.close()
            assertTrue(redis.isClosed)
        }
    }

    @Test
    fun `instances share bounded expiring state without namespace collisions`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { first ->
                GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { second ->
                    run {
                        val sessionKey = first.key(RedisArea.SESSION, "same-identifier")
                        val throttleKey = first.key(RedisArea.THROTTLE, "same-identifier")
                        assertEquals(
                            "gamma:kotlin:v1:session:same-identifier",
                            sessionKey.storageKey,
                        )

                        first.set(sessionKey, "versioned-session", Duration.ofSeconds(30))
                        first.set(throttleKey, "versioned-throttle", Duration.ofSeconds(30))

                        assertEquals("versioned-session", second.get(sessionKey))
                        assertEquals("versioned-throttle", second.get(throttleKey))
                        assertTrue(second.timeToLiveMillis(sessionKey) in 1..30_000)
                        assertTrue(second.ping())

                        val sharedReads = second.readConcurrently(sessionKey, times = 200)
                        assertTrue(sharedReads.all { it == "versioned-session" })

                        assertTrue(
                            first.setIfAbsent(first.key(RedisArea.SESSION, "new"), "one", Duration.ofSeconds(30)),
                        )
                        assertFalse(
                            second.setIfAbsent(first.key(RedisArea.SESSION, "new"), "two", Duration.ofSeconds(30)),
                        )
                        assertEquals("one", second.get(first.key(RedisArea.SESSION, "new")))
                    }
                }
            }
        }
    }

    @Test
    fun `delete and scripts preserve their command results`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { redis ->
                run {
                    val key = redis.key(RedisArea.SESSION, "script-contract")
                    redis.set(key, "stored", Duration.ofMinutes(1))

                    assertEquals(
                        "stored:argument",
                        redis.evaluateString(
                            "return redis.call('GET', KEYS[1]) .. ':' .. ARGV[1]",
                            keys = listOf(key),
                            arguments = listOf("argument"),
                        ),
                    )
                    assertEquals(
                        42L,
                        redis.evaluateLong(
                            "return tonumber(ARGV[1]) + tonumber(ARGV[2])",
                            keys = emptyList(),
                            arguments = listOf("20", "22"),
                        ),
                    )
                    assertEquals(1L, redis.delete(key))
                    assertNull(redis.get(key))
                    assertEquals(0L, redis.delete())
                }
            }
        }
    }

    @Test
    fun `expiry script and argument limits preserve their exact boundaries`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { redis ->
                run {
                    val key = redis.key(RedisArea.SESSION, "boundary-contract")
                    redis.set(key, "value", Duration.ofMillis(1))
                    listOf(Duration.ZERO, Duration.ofMillis(-1), Duration.ofNanos(999_999)).forEach { invalidExpiry ->
                        assertFailsWith<IllegalArgumentException> {
                            redis.set(key, "value", invalidExpiry)
                        }
                    }

                    val maximumScript = "return 1".padEnd(65_536)
                    assertEquals(1L, redis.evaluateLong(maximumScript, emptyList(), emptyList()))
                    assertFailsWith<IllegalArgumentException> {
                        redis.evaluateLong("$maximumScript ", emptyList(), emptyList())
                    }

                    val maximumArgument = "x".repeat(1_048_576)
                    assertEquals(
                        1_048_576L,
                        redis.evaluateLong(
                            "return string.len(ARGV[1])",
                            emptyList(),
                            listOf(maximumArgument),
                        ),
                    )
                    assertFailsWith<IllegalArgumentException> {
                        redis.evaluateLong(
                            "return string.len(ARGV[1])",
                            emptyList(),
                            listOf("${maximumArgument}x"),
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `area scoped scans retain only their own identifiers`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { redis ->
                run {
                    redis.set(
                        redis.key(RedisArea.SESSION, "shared-identifier"),
                        "session",
                        Duration.ofMinutes(1),
                    )
                    redis.set(
                        redis.key(RedisArea.THROTTLE, "shared-identifier"),
                        "throttle",
                        Duration.ofMinutes(1),
                    )

                    assertEquals(
                        listOf("session"),
                        redis.scanEntries(RedisArea.SESSION, maximumEntries = 10).entries.map { it.payload },
                    )
                    assertEquals(
                        listOf("throttle"),
                        redis.scanEntries(RedisArea.THROTTLE, maximumEntries = 10).entries.map { it.payload },
                    )
                    listOf(0, 10_001).forEach { invalidLimit ->
                        assertFailsWith<IllegalArgumentException> {
                            redis.scanEntries(RedisArea.SESSION, invalidLimit)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `entry scan is bounded and redacts returned entries`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { redis ->
                run {
                    repeat(3) { index ->
                        redis.set(
                            redis.key(RedisArea.THROTTLE, "login:$index"),
                            (index + 1).toString(),
                            Duration.ofSeconds(30),
                        )
                    }
                    redis.set(
                        redis.key(RedisArea.SESSION, "not-a-throttle"),
                        "session",
                        Duration.ofSeconds(30),
                    )

                    val snapshot = redis.scanEntries(RedisArea.THROTTLE, maximumEntries = 2)
                    assertEquals(2, snapshot.entries.size)
                    assertFalse(snapshot.complete)
                    assertTrue(snapshot.entries.all { it.identifier.startsWith("login:") })
                    assertTrue(snapshot.entries.all { it.timeToLiveMillis > 0 })
                    assertTrue(snapshot.observedAt <= Instant.now())
                    assertTrue(
                        snapshot.entries.all {
                            snapshot.observedAt.plusMillis(it.timeToLiveMillis) >
                                snapshot.observedAt
                        },
                    )
                    snapshot.entries.forEach { entry ->
                        assertEquals(
                            "RedisEntry(identifier=<redacted>, payload=<redacted>, " +
                                "timeToLiveMillis=${entry.timeToLiveMillis})",
                            entry.toString(),
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `entry scan returns distinct entries while the keyspace changes`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port)).use { redis ->
                run {
                    repeat(200) { index ->
                        redis.set(
                            redis.key(RedisArea.THROTTLE, "stable-$index"),
                            "1",
                            Duration.ofMinutes(1),
                        )
                    }

                    Executors.newSingleThreadExecutor().use { workers ->
                        val churn =
                            workers.submit {
                                repeat(200) { index ->
                                    val key = redis.key(RedisArea.THROTTLE, "changing-${index % 25}")
                                    redis.set(key, "1", Duration.ofMinutes(1))
                                    if (index % 2 == 0) redis.delete(key)
                                }
                            }

                        repeat(25) {
                            val entries = redis.scanEntries(RedisArea.THROTTLE, maximumEntries = 50).entries
                            assertTrue(entries.size <= 50)
                            assertEquals(entries.size, entries.map { entry -> entry.identifier }.distinct().size)
                        }
                        churn.get()
                    }
                }
            }
        }
    }

    @Test
    @Suppress("MissingUseCall") // Both unsafely constructed clients are expected to fail before ownership begins.
    fun `authentication and connection failures are sanitized and fail closed`() {
        RedisTestEnvironment("correct-secret").use { redisServer ->
            val failure =
                assertFailsWith<RedisUnavailable> {
                    GammaRedis(
                        RedisSettings(redisServer.host, redisServer.port, "wrong-secret".toCharArray()),
                    )
                }
            assertEquals("Redis is unavailable during startup", failure.message)
            assertFalse(failure.toString().contains("wrong-secret"))

            GammaRedis(
                RedisSettings(redisServer.host, redisServer.port, "correct-secret".toCharArray()),
            ).use { redis ->
                assertTrue(run { redis.ping() })
            }
        }

        assertFailsWith<RedisUnavailable> {
            GammaRedis(
                RedisSettings("127.0.0.1", 1, timeouts = RedisTimeouts(command = Duration.ofMillis(200))),
            )
        }
    }

    @Test
    fun `logical Redis databases remain isolated`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(RedisSettings(redisServer.host, redisServer.port, database = 0)).use { databaseZero ->
                GammaRedis(RedisSettings(redisServer.host, redisServer.port, database = 1)).use { databaseOne ->
                    val zeroKey = databaseZero.key(RedisArea.THROTTLE, "database-contract")
                    val oneKey = databaseOne.key(RedisArea.THROTTLE, "database-contract")
                    run {
                        databaseZero.set(zeroKey, "zero", Duration.ofMinutes(1))
                        databaseOne.set(oneKey, "one", Duration.ofMinutes(1))
                        assertEquals("zero", databaseZero.get(zeroKey))
                        assertEquals("one", databaseOne.get(oneKey))
                    }
                }
            }
        }
    }

    @Test
    fun `commands exceeding the configured timeout fail closed`() {
        RedisTestEnvironment().use { redisServer ->
            GammaRedis(
                RedisSettings(
                    redisServer.host,
                    redisServer.port,
                    timeouts = RedisTimeouts(command = Duration.ofMillis(50)),
                ),
            ).use { redis ->
                val failure =
                    assertFailsWith<RedisUnavailable> {
                        run {
                            redis.evaluateLong(
                                "local n = 0; for i = 1, 100000000 do n = n + i end; return n",
                                emptyList(),
                                emptyList(),
                            )
                        }
                    }
                assertEquals("Redis operation failed", failure.message)
            }
        }
    }

    @Test
    @Suppress("MissingUseCall") // This test closes explicitly so it can characterize use after shutdown.
    fun `invalid keys payloads and use after shutdown never produce state`() {
        RedisTestEnvironment().use { redisServer ->
            val redis = GammaRedis(RedisSettings(redisServer.host, redisServer.port))
            assertEquals(
                256,
                redis
                    .key(RedisArea.SESSION, "a".repeat(256))
                    .storageKey
                    .substringAfterLast(':')
                    .length,
            )
            listOf("", "a".repeat(257), "contains/slash").forEach { invalidIdentifier ->
                assertFailsWith<IllegalArgumentException>(invalidIdentifier) {
                    redis.key(RedisArea.SESSION, invalidIdentifier)
                }
            }
            assertFailsWith<IllegalArgumentException> {
                run {
                    redis.set(
                        redis.key(RedisArea.SESSION, "oversized"),
                        "x".repeat(1_048_577),
                        Duration.ofSeconds(30),
                    )
                }
            }
            assertEquals("GammaRedisKey(<redacted>)", redis.key(RedisArea.SESSION, "secret-id").toString())
            assertNull(run { redis.get(redis.key(RedisArea.SESSION, "oversized")) })

            redis.close()
            assertFailsWith<RedisUnavailable> { run { redis.ping() } }
        }
    }
}

private fun GammaRedis.readConcurrently(
    key: GammaRedisKey,
    times: Int,
): List<String?> =
    Executors.newVirtualThreadPerTaskExecutor().use { workers ->
        List(times) { workers.submit<String?> { get(key) } }.map { it.get() }
    }
