package it.chalmers.gamma.throttling

import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThrottleTypesTest {
    @Test
    fun `limits reject unusable attempt counts and windows`() {
        val key = ThrottleKey("authentication:test")

        assertEquals(
            "Maximum attempts must be positive",
            assertFailsWith<IllegalArgumentException> {
                FixedWindowLimit(0, Duration.ZERO)
            }.message,
        )
        assertEquals(
            "Maximum attempts must be positive",
            assertFailsWith<IllegalArgumentException> {
                ThrottleLimit(key, 0, Duration.ZERO, ThrottleCharge.FAILED_ATTEMPT)
            }.message,
        )
        listOf(Duration.ZERO, Duration.ofSeconds(-1), Duration.ofNanos(1)).forEach { window ->
            assertEquals(
                "Throttle window must be at least one millisecond",
                assertFailsWith<IllegalArgumentException> {
                    FixedWindowLimit(1, window)
                }.message,
            )
            assertEquals(
                "Throttle window must be at least one millisecond",
                assertFailsWith<IllegalArgumentException> {
                    ThrottleLimit(key, 1, window, ThrottleCharge.FAILED_ATTEMPT)
                }.message,
            )
        }
        val minimumLimit = FixedWindowLimit(1, FixedWindowLimit.MINIMUM_WINDOW)
        assertEquals(1, minimumLimit.maximumAttempts)
        assertEquals(Duration.ofMillis(1), minimumLimit.window)
        assertEquals(
            ThrottleLimit.MAXIMUM_WINDOW,
            ThrottleLimit(
                key,
                1,
                ThrottleLimit.MAXIMUM_WINDOW,
                ThrottleCharge.FAILED_ATTEMPT,
            ).window,
        )
        assertEquals(FixedWindowLimit.MAXIMUM_WINDOW, ThrottleLimit.MAXIMUM_WINDOW)
        assertEquals(FixedWindowLimit.MINIMUM_WINDOW, ThrottleLimit.MINIMUM_WINDOW)
        assertEquals(
            "Throttle window must not exceed 365 days",
            assertFailsWith<IllegalArgumentException> {
                ThrottleLimit(
                    key,
                    1,
                    ThrottleLimit.MAXIMUM_WINDOW.plusMillis(1),
                    ThrottleCharge.FAILED_ATTEMPT,
                )
            }.message,
        )
        assertEquals(
            "Throttle window must not exceed 365 days",
            assertFailsWith<IllegalArgumentException> {
                FixedWindowLimit(1, FixedWindowLimit.MAXIMUM_WINDOW.plusMillis(1))
            }.message,
        )
        assertFailsWith<IllegalArgumentException> {
            ThrottleLimit(key, 1, Duration.ofMillis(Long.MAX_VALUE), ThrottleCharge.FAILED_ATTEMPT)
        }
    }

    // Deliberately exercises the mutation path exposed to JVM callers of the read-only List API.
    @Suppress("DontDowncastCollectionTypes")
    @Test
    fun `reservation requests own a bounded set of distinct limits`() {
        val first = limit("first")
        val source = mutableListOf(first)
        val request = ThrottleReservationRequest(source)

        source.clear()

        assertEquals(listOf(first), request.limits)
        assertFailsWith<UnsupportedOperationException> {
            (request.limits as MutableList<ThrottleLimit>).clear()
        }
        assertEquals(listOf(first), request.limits)
        assertFailsWith<IllegalArgumentException> { ThrottleReservationRequest(emptyList()) }
        assertFailsWith<IllegalArgumentException> { ThrottleReservationRequest(listOf(first, first.copy())) }
        assertEquals(
            8,
            ThrottleReservationRequest(List(8) { index -> limit("limit-$index") }).limits.size,
        )
        assertFailsWith<IllegalArgumentException> {
            ThrottleReservationRequest(List(9) { index -> limit("limit-$index") })
        }
    }

    // Deliberately exercises the mutation path exposed to JVM callers of the read-only List API.
    @Suppress("DontDowncastCollectionTypes")
    @Test
    fun `administrative values validate counts copy entries and redact keys`() {
        val key = ThrottleKey("authentication:sensitive-subject")
        val entry = ThrottleEntry(key, attempts = 2, expiresAt = Instant.EPOCH)
        val source = mutableListOf(entry)
        val snapshot = ThrottleEntrySnapshot(source, complete = false)

        source.clear()

        assertEquals(listOf(entry), snapshot.entries)
        assertFailsWith<UnsupportedOperationException> {
            (snapshot.entries as MutableList<ThrottleEntry>).clear()
        }
        assertEquals(listOf(entry), snapshot.entries)
        assertFalse(snapshot.complete)
        assertFailsWith<IllegalArgumentException> { ThrottleEntry(key, attempts = 0, expiresAt = Instant.EPOCH) }
        assertFalse(entry.toString().contains(key.value))
        assertFalse(snapshot.toString().contains(key.value))
        assertFalse(limit("redacted-key").toString().contains("redacted-key"))
        assertFalse(ThrottleReservationRequest(listOf(limit("request-key"))).toString().contains("request-key"))
        assertTrue(snapshot.toString().contains("complete=false"))
    }

    @Test
    fun `entry scan limits expose the supported platform range`() {
        assertEquals(1_000, ThrottleEntryLimit.DEFAULT.value)
        assertEquals(1, ThrottleEntryLimit(1).value)
        assertEquals(10_000, ThrottleEntryLimit(10_000).value)
        assertFailsWith<IllegalArgumentException> { ThrottleEntryLimit(0) }
        assertFailsWith<IllegalArgumentException> { ThrottleEntryLimit(10_001) }
    }

    private fun limit(key: String) =
        ThrottleLimit(
            ThrottleKey(key),
            maximumAttempts = 3,
            window = Duration.ofMinutes(1),
            charge = ThrottleCharge.FAILED_ATTEMPT,
        )
}
