package it.chalmers.gamma

import it.chalmers.gamma.platform.redis.GammaRedis
import org.springframework.context.SmartLifecycle

/** Reopens Gamma's command connection when Spring resumes its Lettuce factory. */
class RedisLifecycle(
    private val redis: GammaRedis,
) : SmartLifecycle {
    // The default late phase starts after Lettuce and stops before it. Keeping this
    // adapter here avoids exposing Spring lifecycle types through the context APIs.
    override fun start() = redis.start()

    override fun stop() = redis.stop()

    override fun isRunning(): Boolean = redis.isRunning()
}
