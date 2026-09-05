package it.chalmers.gamma.testing

import org.testcontainers.containers.GenericContainer

class RedisTestEnvironment(
    password: String? = null,
) : AutoCloseable {
    private val container = RedisContainer(REDIS_IMAGE)

    val host: String get() = container.host
    val port: Int get() = container.getMappedPort(REDIS_PORT)
    val password: String? = password?.takeIf(String::isNotBlank)

    init {
        container.withExposedPorts(REDIS_PORT)
        this.password?.let { configuredPassword ->
            container.withCommand("redis-server", "--requirepass", configuredPassword)
        }
        container.start()
    }

    override fun close() = container.stop()

    private class RedisContainer(
        image: String,
    ) : GenericContainer<RedisContainer>(image)

    private companion object {
        const val REDIS_PORT = 6379
        const val REDIS_IMAGE =
            "redis@sha256:ff02b58f971e7d7d156a1267e283fcbbeee91773b6aa36c49dac28ecfe28eadf"
    }
}
