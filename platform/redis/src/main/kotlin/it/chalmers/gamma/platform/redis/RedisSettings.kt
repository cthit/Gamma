package it.chalmers.gamma.platform.redis

import java.time.Duration

class RedisSettings(
    val host: String,
    val port: Int,
    password: CharArray? = null,
    val database: Int = 0,
    val timeouts: RedisTimeouts = RedisTimeouts(),
) {
    private val password = password?.copyOf()

    init {
        validateRedisHost(host)
        require(port in 1..65_535) { "Redis port must be an integer between 1 and 65535" }
        require(database >= 0) { "Redis database must be a non-negative integer" }
        require(!timeouts.command.isZero && !timeouts.command.isNegative) { "Redis command timeout must be positive" }
        require(
            !timeouts.connect.isZero && !timeouts.connect.isNegative,
        ) { "Redis connection timeout must be positive" }
    }

    /** A caller-owned copy for the connection adapter. */
    fun passwordCopy(): CharArray? = password?.copyOf()

    override fun equals(other: Any?): Boolean =
        other is RedisSettings &&
            host == other.host &&
            port == other.port &&
            passwordsEqual(password, other.password) &&
            database == other.database &&
            timeouts == other.timeouts

    override fun hashCode(): Int = listOf(host, port, password?.contentHashCode(), database, timeouts).hashCode()

    override fun toString(): String =
        "RedisSettings(host=$host, port=$port, password=${redacted(password)}, database=$database, " +
            "commandTimeout=${timeouts.command}, connectTimeout=${timeouts.connect})"
}

data class RedisTimeouts(
    val command: Duration = Duration.ofSeconds(2),
    val connect: Duration = command,
)

private fun validateRedisHost(host: String) {
    require(host.isNotBlank()) { "Redis host must not be blank" }
    require(host.none(Char::isWhitespace)) { "Redis host must not contain whitespace" }
    require('/' !in host && (':' !in host || host.isIpv6Address())) {
        "Redis host must be a host name or IP address without a scheme or port"
    }
}

private fun passwordsEqual(
    first: CharArray?,
    second: CharArray?,
): Boolean = first?.contentEquals(second ?: return false) ?: (second == null)

private fun redacted(value: Any?): String = if (value == null) "null" else "<redacted>"

private fun String.isIpv6Address(): Boolean =
    count { it == ':' } >= 2 &&
        removeSurrounding("[", "]").all { it.isDigit() || it.lowercaseChar() in 'a'..'f' || it == ':' }
