package it.chalmers.gamma.throttling

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.platform.redis.RedisArea
import it.chalmers.gamma.platform.redis.RedisSettings
import it.chalmers.gamma.platform.redis.RedisUnavailable
import it.chalmers.gamma.testing.RedisTestEnvironment
import java.time.Duration
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RedisThrottlingIntegrationTest {
    @Test
    fun `limits resets and manages counters across Redis instances`() {
        RedisTestEnvironment().use { server ->
            val settings = RedisSettings(server.host, server.port)
            GammaRedis(settings).use { firstRedis ->
                GammaRedis(settings).use { secondRedis ->
                    val first = RedisThrottling(firstRedis)
                    val second = RedisThrottling(secondRedis)
                    run {
                        val key = ThrottleKey("login:127.0.0.1")
                        assertTrue(first.charge(key, 2))
                        assertTrue(second.charge(key, 2))
                        assertFalse(first.charge(key, 2))
                        assertEquals(
                            3L,
                            second
                                .snapshot()
                                .entries
                                .single()
                                .attempts,
                        )

                        second.delete(key)
                        assertTrue(first.snapshot().entries.isEmpty())

                        val expiring = ThrottleKey("email:example")
                        assertTrue(first.charge(expiring, 1, Duration.ofMillis(50)))
                        assertFalse(second.charge(expiring, 1, Duration.ofMillis(50)))
                        Thread.sleep(75)
                        assertTrue(second.charge(expiring, 1, Duration.ofHours(1)))
                        assertEquals(
                            1L,
                            first
                                .snapshot()
                                .entries
                                .single()
                                .attempts,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `concurrent instances atomically enforce the shared attempt limit`() {
        RedisTestEnvironment().use { server ->
            val settings = RedisSettings(server.host, server.port)
            GammaRedis(settings).use { firstRedis ->
                GammaRedis(settings).use { secondRedis ->
                    val adapters = listOf(RedisThrottling(firstRedis), RedisThrottling(secondRedis))
                    val allowed =
                        Executors.newVirtualThreadPerTaskExecutor().use { workers ->
                            List(100) { attempt ->
                                workers.submit<Boolean> {
                                    adapters[attempt % adapters.size]
                                        .charge(ThrottleKey("concurrent"), 10, Duration.ofMinutes(1))
                                }
                            }.map { it.get() }
                        }

                    assertEquals(10, allowed.count(Boolean::not).let { allowed.size - it })
                    assertEquals(
                        100L,
                        run {
                            adapters
                                .first()
                                .snapshot()
                                .entries
                                .single()
                                .attempts
                        },
                    )
                }
            }
        }
    }

    @Test
    fun `concurrent reservations never overshoot a shared limit`() {
        RedisTestEnvironment().use { server ->
            val settings = RedisSettings(server.host, server.port)
            GammaRedis(settings).use { firstRedis ->
                GammaRedis(settings).use { secondRedis ->
                    val adapters = listOf(RedisThrottling(firstRedis), RedisThrottling(secondRedis))
                    val key = ThrottleKey("reserved-concurrently")
                    val limits =
                        listOf(
                            ThrottleLimit(
                                key,
                                maximumAttempts = 10,
                                window = Duration.ofMinutes(1),
                                charge = ThrottleCharge.FAILED_ATTEMPT,
                            ),
                        )

                    val reservations =
                        Executors.newVirtualThreadPerTaskExecutor().use { workers ->
                            List(100) { attempt ->
                                workers.submit<ThrottleReservation?> {
                                    adapters[attempt % adapters.size].reserve(limits)
                                }
                            }.map { it.get() }
                        }

                    assertEquals(10, reservations.count { it != null })
                    assertEquals(
                        10L,
                        run {
                            adapters
                                .first()
                                .snapshot()
                                .entries
                                .single()
                                .attempts
                        },
                    )
                }
            }
        }
    }

    @Test
    fun `multi limit reservation charges every counter or none`() {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                val throttling = RedisThrottling(redis)
                val exhausted = ThrottleKey("reservation-exhausted")
                val untouched = ThrottleKey("reservation-untouched")
                run {
                    assertTrue(
                        throttling.reserve(
                            listOf(
                                ThrottleLimit(
                                    exhausted,
                                    maximumAttempts = 1,
                                    window = Duration.ofMinutes(1),
                                    charge = ThrottleCharge.EVERY_ATTEMPT,
                                ),
                            ),
                        ) != null,
                    )

                    assertEquals(
                        null,
                        throttling.reserve(
                            listOf(
                                ThrottleLimit(
                                    untouched,
                                    maximumAttempts = 10,
                                    window = Duration.ofMinutes(1),
                                    charge = ThrottleCharge.FAILED_ATTEMPT,
                                ),
                                ThrottleLimit(
                                    exhausted,
                                    maximumAttempts = 1,
                                    window = Duration.ofMinutes(1),
                                    charge = ThrottleCharge.EVERY_ATTEMPT,
                                ),
                            ),
                        ),
                    )
                }

                val entries = run { throttling.snapshot().entries }.associateBy { it.key }
                assertEquals(1L, entries.getValue(exhausted).attempts)
                assertEquals(null, entries[untouched])
            }
        }
    }

    @Test
    fun `success refund is idempotent and retains every attempt plus concurrent failures`() {
        RedisTestEnvironment().use { server ->
            val settings = RedisSettings(server.host, server.port)
            GammaRedis(settings).use { firstRedis ->
                GammaRedis(settings).use { secondRedis ->
                    val first = RedisThrottling(firstRedis)
                    val second = RedisThrottling(secondRedis)
                    val everyAttempt = ThrottleKey("reservation-every-attempt")
                    val failedAttempt = ThrottleKey("reservation-failed-attempt")
                    val successfulReservation =
                        run {
                            checkNotNull(
                                first.reserve(
                                    listOf(
                                        ThrottleLimit(
                                            everyAttempt,
                                            maximumAttempts = 100,
                                            window = Duration.ofMinutes(1),
                                            charge = ThrottleCharge.EVERY_ATTEMPT,
                                        ),
                                        ThrottleLimit(
                                            failedAttempt,
                                            maximumAttempts = 100,
                                            window = Duration.ofMinutes(1),
                                            charge = ThrottleCharge.FAILED_ATTEMPT,
                                        ),
                                    ),
                                ),
                            )
                        }
                    val failureLimit =
                        listOf(
                            ThrottleLimit(
                                failedAttempt,
                                maximumAttempts = 100,
                                window = Duration.ofMinutes(1),
                                charge = ThrottleCharge.FAILED_ATTEMPT,
                            ),
                        )

                    refundWhileReservingFailures(first, second, successfulReservation, failureLimit)
                    first.refund(successfulReservation)

                    val entries = run { second.snapshot().entries }.associateBy { it.key }
                    assertEquals(1L, entries.getValue(everyAttempt).attempts)
                    assertEquals(25L, entries.getValue(failedAttempt).attempts)
                }
            }
        }
    }

    @Test
    fun `failed reservation completion removes its marker and cannot later refund the charge`() {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                val throttling = RedisThrottling(redis)
                val key = ThrottleKey("committed-failure")
                val reservation =
                    run {
                        checkNotNull(
                            throttling.reserve(
                                listOf(
                                    ThrottleLimit(
                                        key,
                                        maximumAttempts = 10,
                                        window = Duration.ofMinutes(5),
                                        charge = ThrottleCharge.FAILED_ATTEMPT,
                                    ),
                                ),
                            ),
                        )
                    }

                run {
                    throttling.commit(reservation)
                    throttling.commit(reservation)
                    throttling.refund(reservation)
                }

                assertEquals(
                    1L,
                    run {
                        throttling
                            .snapshot()
                            .entries
                            .single()
                            .attempts
                    },
                )
            }
        }
    }

    @Test
    fun `reservation refund completes synchronously`() {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                val throttling = RedisThrottling(redis)
                val reservation =
                    run {
                        checkNotNull(
                            throttling.reserve(
                                listOf(
                                    ThrottleLimit(
                                        ThrottleKey("cancelled-refund"),
                                        maximumAttempts = 10,
                                        window = Duration.ofMinutes(5),
                                        charge = ThrottleCharge.FAILED_ATTEMPT,
                                    ),
                                ),
                            ),
                        )
                    }

                throttling.refund(reservation)

                assertEquals(emptyList(), run { throttling.snapshot().entries })
            }
        }
    }

    @Test
    fun `reservation follows an existing counter expiry and cannot refund its replacement`() {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                val throttling = RedisThrottling(redis)
                val key = ThrottleKey("existing-short-refund-window")
                assertTrue(
                    run {
                        throttling.charge(
                            key,
                            maximumAttempts = 10,
                            window = Duration.ofSeconds(2),
                        )
                    },
                )
                Thread.sleep(500)
                val limit =
                    ThrottleLimit(
                        key,
                        maximumAttempts = 10,
                        window = Duration.ofMinutes(5),
                        charge = ThrottleCharge.FAILED_ATTEMPT,
                    )
                val expiredReservation = run { checkNotNull(throttling.reserve(listOf(limit))) }
                assertEquals(
                    2L,
                    run {
                        throttling
                            .snapshot()
                            .entries
                            .single()
                            .attempts
                    },
                )

                run {
                    Thread.sleep(1_700)
                    assertEquals(emptyList(), throttling.snapshot().entries)
                    checkNotNull(throttling.reserve(listOf(limit)))
                    throttling.refund(expiredReservation)
                }

                assertEquals(
                    1L,
                    run {
                        throttling
                            .snapshot()
                            .entries
                            .single()
                            .attempts
                    },
                )
            }
        }
    }

    @Test
    fun `cleanup failure propagates after Redis loss`() {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                val throttling = RedisThrottling(redis)
                val reservation =
                    run {
                        checkNotNull(
                            throttling.reserve(
                                listOf(
                                    ThrottleLimit(
                                        ThrottleKey("cancelled-cleanup-failure"),
                                        maximumAttempts = 10,
                                        window = Duration.ofMinutes(1),
                                        charge = ThrottleCharge.FAILED_ATTEMPT,
                                    ),
                                ),
                            ),
                        )
                    }
                redis.close()
                assertFailsWith<RedisUnavailable> { throttling.refund(reservation) }
            }
        }
    }

    @Test
    fun `malformed state and Redis loss fail closed without exposing keys`() {
        RedisTestEnvironment().use { server ->
            GammaRedis(RedisSettings(server.host, server.port)).use { redis ->
                val throttling = RedisThrottling(redis)
                val key = ThrottleKey.digest("password-reset", "sensitive@example.org")
                run {
                    redis.set(
                        redis.key(RedisArea.THROTTLE, key.value),
                        "unknown:payload",
                        Duration.ofMinutes(1),
                    )
                }
                val malformed =
                    assertFailsWith<RedisUnavailable> {
                        run { throttling.charge(key, 3) }
                    }
                assertFalse(malformed.toString().contains(key.value))

                redis.close()
                assertFailsWith<RedisUnavailable> {
                    run { throttling.charge(ThrottleKey("unavailable"), 3) }
                }
            }
        }
    }
}

private fun refundWhileReservingFailures(
    first: RedisThrottling,
    second: RedisThrottling,
    successfulReservation: ThrottleReservation,
    failureLimit: List<ThrottleLimit>,
) {
    Executors.newVirtualThreadPerTaskExecutor().use { workers ->
        val refund = workers.submit { first.refund(successfulReservation) }
        val reservations =
            List(25) { attempt ->
                workers.submit<ThrottleReservation> {
                    checkNotNull(listOf(first, second)[attempt % 2].reserve(failureLimit))
                }
            }
        refund.get()
        reservations.forEach { it.get() }
    }
}

private fun RedisThrottling.reserve(limits: List<ThrottleLimit>): ThrottleReservation? =
    reserve(ThrottleReservationRequest(limits))
