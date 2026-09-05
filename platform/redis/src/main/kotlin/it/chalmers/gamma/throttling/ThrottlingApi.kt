package it.chalmers.gamma.throttling

import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.ArrayList
import java.util.Base64
import java.util.Collections

@JvmInline
value class ThrottleKey(
    val value: String,
) {
    init {
        require(value.matches(VALID_KEY_PATTERN)) {
            "Throttle keys may only contain letters, numbers, colons, dots, at signs, underscores, or dashes"
        }
    }

    companion object {
        private val VALID_KEY_PATTERN = Regex("^[A-Za-z0-9:@._-]{1,200}$")
        private val VALID_NAMESPACE_PATTERN = Regex("^[A-Za-z0-9._-]{1,80}$")

        fun digest(
            namespace: String,
            value: String,
        ): ThrottleKey {
            require(namespace.matches(VALID_NAMESPACE_PATTERN)) { "Throttle namespace is invalid" }
            val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
            val encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
            return ThrottleKey("$namespace:$encoded")
        }
    }

    override fun toString(): String = "ThrottleKey(<redacted>)"
}

data class ThrottleEntry(
    val key: ThrottleKey,
    val attempts: Long,
    val expiresAt: Instant,
) {
    init {
        require(attempts > 0) { "Throttle attempts must be positive" }
    }

    override fun toString(): String = "ThrottleEntry(key=<redacted>, attempts=$attempts, expiresAt=$expiresAt)"
}

enum class ThrottleCharge {
    EVERY_ATTEMPT,
    FAILED_ATTEMPT,
}

data class FixedWindowLimit(
    val maximumAttempts: Int,
    val window: Duration,
) {
    init {
        require(maximumAttempts > 0) { "Maximum attempts must be positive" }
        require(window >= MINIMUM_WINDOW) { "Throttle window must be at least one millisecond" }
        require(window <= MAXIMUM_WINDOW) { "Throttle window must not exceed 365 days" }
    }

    companion object {
        val MINIMUM_WINDOW: Duration = Duration.ofMillis(1)
        val MAXIMUM_WINDOW: Duration = Duration.ofDays(365)
    }
}

data class ThrottleLimit(
    val key: ThrottleKey,
    val maximumAttempts: Int,
    val window: Duration,
    val charge: ThrottleCharge,
) {
    init {
        FixedWindowLimit(maximumAttempts, window)
    }

    override fun toString(): String =
        "ThrottleLimit(key=<redacted>, maximumAttempts=$maximumAttempts, window=$window, charge=$charge)"

    companion object {
        val MINIMUM_WINDOW: Duration = FixedWindowLimit.MINIMUM_WINDOW
        val MAXIMUM_WINDOW: Duration = FixedWindowLimit.MAXIMUM_WINDOW
    }
}

class ThrottleReservationRequest(
    limits: List<ThrottleLimit>,
) {
    val limits: List<ThrottleLimit> = unmodifiableCopy(limits)

    init {
        require(this.limits.isNotEmpty()) { "A throttle reservation must contain at least one limit" }
        require(this.limits.size <= MAXIMUM_RESERVATION_LIMITS) {
            "A throttle reservation may contain at most $MAXIMUM_RESERVATION_LIMITS limits"
        }
        require(
            this.limits
                .map(ThrottleLimit::key)
                .distinct()
                .size == this.limits.size,
        ) {
            "A throttle reservation cannot contain the same key more than once"
        }
    }

    override fun toString(): String = "ThrottleReservationRequest(limits=${limits.size}, keys=<redacted>)"
}

/** Opaque capability returned only by the [ThrottleReservations] implementation that issued it. */
open class ThrottleReservation protected constructor()

interface ThrottleReservations {
    /**
     * Atomically charges every limit, or charges none when any limit is already exhausted.
     *
     * Every returned reservation must be completed exactly once with [refund] or [commit]. Call
     * [refund] only when authentication succeeds or no authentication work was started.
     * The refund removes one charge from each [ThrottleCharge.FAILED_ATTEMPT] limit while retaining
     * [ThrottleCharge.EVERY_ATTEMPT] limits. Call [commit] for a definitive failure or an unknown
     * outcome. The reservation lease only bounds marker cleanup after an interrupted, unclassified
     * attempt; its expiry never rolls back charged counters.
     */
    fun reserve(request: ThrottleReservationRequest): ThrottleReservation?

    /** Refunds this reservation at most once without deleting concurrent charges. */
    fun refund(reservation: ThrottleReservation)

    /** Completes this reservation while retaining every charged attempt. */
    fun commit(reservation: ThrottleReservation)
}

interface FixedWindowThrottling {
    /** Records an attempt and returns whether it remains within the fixed-window limit. */
    fun charge(
        key: ThrottleKey,
        maximumAttempts: Int,
        window: Duration = Duration.ofHours(24),
    ): Boolean
}

class ThrottleEntrySnapshot(
    entries: List<ThrottleEntry>,
    val complete: Boolean,
) {
    val entries: List<ThrottleEntry> = unmodifiableCopy(entries)

    override fun toString(): String = "ThrottleEntrySnapshot(entries=${entries.size}, complete=$complete)"
}

@JvmInline
value class ThrottleEntryLimit(
    val value: Int,
) {
    init {
        require(value in 1..MAXIMUM_THROTTLE_ENTRY_LIMIT) {
            "Throttle entry limit must be between 1 and $MAXIMUM_THROTTLE_ENTRY_LIMIT"
        }
    }

    companion object {
        val DEFAULT = ThrottleEntryLimit(1_000)
    }
}

interface ThrottleEntryStore {
    /** Returns at most [limit] entries and says explicitly whether the scan reached its end. */
    fun snapshot(limit: ThrottleEntryLimit = ThrottleEntryLimit.DEFAULT): ThrottleEntrySnapshot

    fun delete(key: ThrottleKey)
}

private const val MAXIMUM_THROTTLE_ENTRY_LIMIT = 10_000
private const val MAXIMUM_RESERVATION_LIMITS = 8

private fun <T> unmodifiableCopy(values: List<T>): List<T> = Collections.unmodifiableList(ArrayList(values))
