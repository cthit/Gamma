@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("GammaRedisKt")

package it.chalmers.gamma.platform.redis

import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor
import io.lettuce.core.SetArgs
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Gamma's thin command layer over Spring Boot's Lettuce client.
 *
 * A single Lettuce connection is safe for concurrent callers. Commands are synchronous, and the
 * bounded request queue rejects work while disconnected instead of accumulating it indefinitely.
 * After a mutating command is dispatched, [RedisUnavailable] means its outcome
 * may be unknown. Callers may retry only commands and scripts that are safe to apply again.
 */
class GammaRedis private constructor(
    private val connectionFactory: LettuceConnectionFactory,
    private val ownsConnectionFactory: Boolean,
) : AutoCloseable {
    constructor(connectionFactory: LettuceConnectionFactory) : this(connectionFactory, ownsConnectionFactory = false)

    /** Standalone construction is retained for integration tests and command-line consumers. */
    constructor(
        settings: RedisSettings,
    ) : this(standaloneRedisConnectionFactory(settings), ownsConnectionFactory = true)

    private val closed = AtomicBoolean(false)

    @Volatile
    private var connection: RedisCommandConnection = openRedisConnection(connectionFactory, ownsConnectionFactory)

    @Volatile
    private var running = true
    private val keyspace = RedisKeyspace()

    fun key(
        area: RedisArea,
        identifier: String,
    ): GammaRedisKey {
        validateKeyIdentifier(identifier)
        return keyspace.key(area, identifier)
    }

    private fun validateKeyIdentifier(identifier: String) {
        require(identifier.isNotEmpty() && identifier.length <= MAXIMUM_KEY_IDENTIFIER_LENGTH) {
            "Redis key identifier must contain between 1 and $MAXIMUM_KEY_IDENTIFIER_LENGTH characters"
        }
        require(identifier.all { it.isLetterOrDigit() || it in SAFE_KEY_PUNCTUATION }) {
            "Redis key identifier contains unsupported characters"
        }
    }

    @Suppress("ExplicitCollectionElementAccessMethod") // Lettuce GET is a Redis command, not collection access.
    fun get(key: GammaRedisKey): String? {
        val payload = command { connection.get(key.storageKey) }
        if (payload != null && payload.toByteArray(Charsets.UTF_8).size > MAXIMUM_PAYLOAD_BYTES) {
            return null
        }
        return payload
    }

    /** [RedisUnavailable] after dispatch does not prove the write was rejected. */
    fun set(
        key: GammaRedisKey,
        payload: String,
        timeToLive: Duration,
    ) {
        validatePayload(payload)
        validateTimeToLive(timeToLive)
        command { connection.set(key.storageKey, payload, SetArgs.Builder.px(timeToLive.toMillis())) }
    }

    /** [RedisUnavailable] after dispatch does not prove whether this call stored it. */
    fun setIfAbsent(
        key: GammaRedisKey,
        payload: String,
        timeToLive: Duration,
    ): Boolean {
        validatePayload(payload)
        validateTimeToLive(timeToLive)
        return command {
            connection.set(
                key.storageKey,
                payload,
                SetArgs.Builder.nx().px(timeToLive.toMillis()),
            )
        } == "OK"
    }

    /** [RedisUnavailable] after dispatch does not prove the keys remain. */
    @Suppress("SpreadOperator") // Lettuce exposes UNLINK through a Java vararg API.
    fun delete(vararg keys: GammaRedisKey): Long {
        if (keys.isEmpty()) return 0
        val currentKeys = keys.map(GammaRedisKey::storageKey).distinct()
        return command { connection.unlink(*currentKeys.toTypedArray()) }
    }

    internal fun timeToLiveMillis(key: GammaRedisKey): Long = command { connection.pttl(key.storageKey) }

    /** Scripts may mutate Redis; after failure, retry only when reapplying the script is safe. */
    @Suppress("SpreadOperator") // Lettuce exposes script arguments through a Java vararg API.
    fun evaluateLong(
        script: String,
        keys: List<GammaRedisKey>,
        arguments: List<String>,
    ): Long {
        validateScript(script, arguments)
        val storageKeys = keys.map(GammaRedisKey::storageKey)
        return command {
            connection.evaluateLong(
                script,
                storageKeys.toTypedArray(),
                *arguments.toTypedArray(),
            )
        }
    }

    /** Scripts may mutate Redis; after failure, retry only when reapplying the script is safe. */
    @Suppress("SpreadOperator") // Lettuce exposes script arguments through a Java vararg API.
    fun evaluateString(
        script: String,
        keys: List<GammaRedisKey>,
        arguments: List<String>,
    ): String? {
        validateScript(script, arguments)
        val storageKeys = keys.map(GammaRedisKey::storageKey)
        return command {
            connection.evaluateString(
                script,
                storageKeys.toTypedArray(),
                *arguments.toTypedArray(),
            )
        }?.takeIf { it.toByteArray(Charsets.UTF_8).size <= MAXIMUM_PAYLOAD_BYTES }
    }

    @Suppress("ExplicitCollectionElementAccessMethod") // Lettuce GET is a Redis command, not collection access.
    fun scanEntries(
        area: RedisArea,
        maximumEntries: Int,
    ): RedisEntrySnapshot =
        scanEntries(
            keyspace.prefix(area),
            maximumEntries,
        )

    @Suppress("ExplicitCollectionElementAccessMethod") // Lettuce GET is a Redis command, not collection access.
    private fun scanEntries(
        prefix: String,
        maximumEntries: Int,
    ): RedisEntrySnapshot {
        require(maximumEntries in 1..MAXIMUM_SCAN_ENTRIES) {
            "Redis scan limit must be between 1 and $MAXIMUM_SCAN_ENTRIES"
        }
        val scanArguments = ScanArgs().match("$prefix*").limit(SCAN_BATCH_SIZE)
        val keys = linkedSetOf<String>()
        var cursor = ScanCursor.INITIAL
        var pageWasTruncated = false
        do {
            val page = command { connection.scan(cursor, scanArguments) }
            cursor = page
            for (storageKey in page.keys) {
                if (storageKey !in keys) {
                    if (keys.size == maximumEntries) {
                        pageWasTruncated = true
                        break
                    }
                    keys += storageKey
                }
            }
        } while (!cursor.isFinished && keys.size < maximumEntries)

        val sampledEntries =
            keys.mapNotNull { storageKey ->
                readSampledEntry(prefix, storageKey)
            }
        val observedAt = Instant.now()
        val scanEndedAt = System.nanoTime()
        val entries = sampledEntries.mapNotNull { sampled -> sampled.observedAt(scanEndedAt) }
        return RedisEntrySnapshot(
            entries,
            complete = cursor.isFinished && !pageWasTruncated,
            observedAt = observedAt,
        )
    }

    @Suppress("ExplicitCollectionElementAccessMethod") // Lettuce GET is a Redis command, not collection access.
    private fun readSampledEntry(
        prefix: String,
        storageKey: String,
    ): SampledRedisEntry? {
        val payload = command { connection.get(storageKey) } ?: return null
        if (payload.toByteArray(Charsets.UTF_8).size > MAXIMUM_PAYLOAD_BYTES) return null
        val timeToLiveMeasuredAt = System.nanoTime()
        val timeToLive = command { connection.pttl(storageKey) }
        if (timeToLive <= 0) return null
        return SampledRedisEntry(storageKey.removePrefix(prefix), payload, timeToLive, timeToLiveMeasuredAt)
    }

    fun ping(): Boolean = command(connection::ping) == "PONG"

    val isClosed: Boolean
        get() = closed.get()

    // Spring may stop and restart the Lettuce factory without recreating this bean.
    // The application's lifecycle adapter orders these calls around the factory.
    // A final close still cannot be reversed.
    @Synchronized
    fun start() {
        check(!closed.get()) { "A closed Redis adapter cannot restart" }
        if (running) return
        // A failed resume may be retried. The existing adapter still owns final factory cleanup.
        connection = openRedisConnection(connectionFactory, ownsConnectionFactory = false)
        running = true
    }

    @Synchronized
    fun stop() {
        if (!running) return
        running = false
        connection.close()
    }

    fun isRunning(): Boolean = running

    @Suppress("TooGenericExceptionCaught") // Both resources must be attempted and the first failure retained.
    @Synchronized
    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        var firstFailure: RuntimeException? = null
        try {
            stop()
        } catch (failure: RuntimeException) {
            firstFailure = failure
        }
        if (ownsConnectionFactory) {
            try {
                connectionFactory.destroy()
            } catch (failure: RuntimeException) {
                if (firstFailure == null) firstFailure = failure else firstFailure.addSuppressed(failure)
            }
        }
        firstFailure?.let { throw it }
    }

    private fun <T> command(operation: () -> T): T =
        try {
            operation()
        } catch (_: RuntimeException) {
            throw RedisUnavailable("Redis operation failed")
        }

    private fun validatePayload(payload: String) {
        require(payload.toByteArray(Charsets.UTF_8).size <= MAXIMUM_PAYLOAD_BYTES) {
            "Redis payload exceeds the $MAXIMUM_PAYLOAD_BYTES byte limit"
        }
    }

    private fun validateScript(
        script: String,
        arguments: List<String>,
    ) {
        require(script.toByteArray(Charsets.UTF_8).size <= MAXIMUM_SCRIPT_BYTES) { "Redis script is too large" }
        arguments.forEach(::validatePayload)
    }

    private fun validateTimeToLive(timeToLive: Duration) {
        require(!timeToLive.isZero && !timeToLive.isNegative && timeToLive.toMillis() > 0) {
            "Redis expiry must be at least one millisecond"
        }
    }

    companion object {
        private const val MAXIMUM_KEY_IDENTIFIER_LENGTH = 256
        private const val MAXIMUM_PAYLOAD_BYTES = 1_048_576
        private const val MAXIMUM_SCRIPT_BYTES = 65_536
        private const val MAXIMUM_SCAN_ENTRIES = 10_000
        private const val SCAN_BATCH_SIZE = 100L
        private val SAFE_KEY_PUNCTUATION = setOf('.', '_', ':', '-', '@')

        fun digest(sensitiveValue: String): String =
            Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(MessageDigest.getInstance("SHA-256").digest(sensitiveValue.toByteArray(Charsets.UTF_8)))
    }
}

// Connection failures can include credentials in their diagnostics, so expose only Gamma's stable error.
@Suppress("TooGenericExceptionCaught")
private fun openRedisConnection(
    connectionFactory: LettuceConnectionFactory,
    ownsConnectionFactory: Boolean,
): RedisCommandConnection =
    try {
        redisCommandConnection(connectionFactory)
    } catch (_: RuntimeException) {
        if (ownsConnectionFactory) {
            try {
                connectionFactory.destroy()
            } catch (_: RuntimeException) {
                // The stable startup failure must not retain possibly sensitive cleanup diagnostics.
            }
        }
        throw RedisUnavailable("Redis is unavailable during startup")
    }

private data class SampledRedisEntry(
    val identifier: String,
    val payload: String,
    val timeToLiveMillis: Long,
    val timeToLiveMeasuredAt: Long,
) {
    fun observedAt(observedAtNanos: Long): RedisEntry? {
        val agedTimeToLive = ageRedisTimeToLive(timeToLiveMillis, timeToLiveMeasuredAt, observedAtNanos)
        if (agedTimeToLive <= 0) return null
        return RedisEntry(identifier, payload, agedTimeToLive)
    }
}
