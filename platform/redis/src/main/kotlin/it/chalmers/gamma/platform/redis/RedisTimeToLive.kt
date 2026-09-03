@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("GammaRedisKt")

package it.chalmers.gamma.platform.redis

internal fun ageRedisTimeToLive(
    timeToLiveMillis: Long,
    measuredAtNanos: Long,
    observedAtNanos: Long,
): Long {
    val elapsedNanos = observedAtNanos - measuredAtNanos
    require(elapsedNanos >= 0) { "Redis observation time must not precede TTL measurement" }
    val elapsedMillis =
        if (elapsedNanos == 0L) {
            0L
        } else {
            ((elapsedNanos - 1L) / NANOS_PER_MILLISECOND) + 1L
        }
    return timeToLiveMillis - elapsedMillis
}

private const val NANOS_PER_MILLISECOND = 1_000_000L
