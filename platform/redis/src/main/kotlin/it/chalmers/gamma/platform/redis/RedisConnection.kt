package it.chalmers.gamma.platform.redis

import io.lettuce.core.ClientOptions
import io.lettuce.core.KeyScanCursor
import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor
import io.lettuce.core.ScriptOutputType
import io.lettuce.core.SetArgs
import io.lettuce.core.SocketOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration

internal fun redisCommandConnection(connectionFactory: LettuceConnectionFactory): RedisCommandConnection {
    val client =
        connectionFactory.requiredNativeClient as? RedisClient
            ?: error("Gamma requires a standalone Lettuce Redis client")
    return StandaloneRedisCommandConnection(client.connect())
}

internal fun standaloneRedisConnectionFactory(settings: RedisSettings): LettuceConnectionFactory {
    val standalone = RedisStandaloneConfiguration(settings.host, settings.port)
    standalone.database = settings.database
    val password = settings.passwordCopy()
    try {
        if (password != null && password.isNotEmpty()) standalone.password = RedisPassword.of(password)
    } finally {
        password?.fill('\u0000')
    }

    val options =
        ClientOptions
            .builder()
            .autoReconnect(true)
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .requestQueueSize(MAXIMUM_QUEUED_COMMANDS)
            .socketOptions(SocketOptions.builder().connectTimeout(settings.timeouts.connect).build())
            .timeoutOptions(TimeoutOptions.enabled(settings.timeouts.command))
            .build()
    val clientConfiguration =
        LettuceClientConfiguration
            .builder()
            .clientOptions(options)
            .commandTimeout(settings.timeouts.command)
            .shutdownTimeout(settings.timeouts.command)
            .build()
    return LettuceConnectionFactory(standalone, clientConfiguration).apply {
        afterPropertiesSet()
        start()
    }
}

internal interface RedisCommandConnection {
    fun close()

    fun ping(): String

    fun get(key: String): String?

    fun set(
        key: String,
        value: String,
        arguments: SetArgs,
    ): String?

    fun unlink(vararg keys: String): Long

    fun pttl(key: String): Long

    fun evaluateLong(
        script: String,
        keys: Array<String>,
        vararg arguments: String,
    ): Long

    fun evaluateString(
        script: String,
        keys: Array<String>,
        vararg arguments: String,
    ): String?

    fun scan(
        cursor: ScanCursor,
        arguments: ScanArgs,
    ): KeyScanCursor<String>
}

private class StandaloneRedisCommandConnection(
    private val connection: StatefulRedisConnection<String, String>,
) : RedisCommandConnection {
    private val commands = connection.sync()

    override fun close() = connection.close()

    override fun ping(): String = commands.ping()

    override fun get(key: String): String? = commands[key]

    override fun set(
        key: String,
        value: String,
        arguments: SetArgs,
    ): String? = commands.set(key, value, arguments)

    override fun unlink(vararg keys: String): Long = commands.unlink(*keys)

    override fun pttl(key: String): Long = commands.pttl(key)

    override fun evaluateLong(
        script: String,
        keys: Array<String>,
        vararg arguments: String,
    ): Long = commands.eval(script, ScriptOutputType.INTEGER, keys, *arguments)

    override fun evaluateString(
        script: String,
        keys: Array<String>,
        vararg arguments: String,
    ): String? = commands.eval(script, ScriptOutputType.VALUE, keys, *arguments)

    override fun scan(
        cursor: ScanCursor,
        arguments: ScanArgs,
    ): KeyScanCursor<String> = commands.scan(cursor, arguments)
}

private const val MAXIMUM_QUEUED_COMMANDS = 1_024
