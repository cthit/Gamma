package it.chalmers.gamma.platform.redis

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class RedisSettingsTest {
    @Test
    fun `settings own password buffers and redact diagnostics`() {
        val sourcePassword = "sensitive-password".toCharArray()
        val settings = RedisSettings("redis", 6379, password = sourcePassword)
        sourcePassword.fill('x')

        val firstCopy = checkNotNull(settings.passwordCopy())
        assertContentEquals("sensitive-password".toCharArray(), firstCopy)
        firstCopy.fill('x')
        assertContentEquals("sensitive-password".toCharArray(), settings.passwordCopy())
        assertFalse("sensitive-password" in settings.toString())

        val equalSettings = RedisSettings("redis", 6379, password = "sensitive-password".toCharArray())
        assertEquals(settings, equalSettings)
        assertEquals(settings.hashCode(), equalSettings.hashCode())
        assertNotEquals(settings, RedisSettings("redis", 6379, password = "different".toCharArray()))
    }

    @Test
    fun `rejects invalid standalone addresses`() {
        listOf("", "with space", "redis://cache", "cache:6379").forEach { host ->
            assertFailsWith<IllegalArgumentException>(host) { RedisSettings(host, 6379) }
        }
        assertEquals("::1", RedisSettings("::1", 6379).host)
        listOf(0, 65_536).forEach { port ->
            assertFailsWith<IllegalArgumentException>(port.toString()) { RedisSettings("redis", port) }
        }
    }

    @Test
    fun `rejects invalid database and timeouts`() {
        assertFailsWith<IllegalArgumentException> { RedisSettings("redis", 6379, database = -1) }
        assertFailsWith<IllegalArgumentException> {
            RedisSettings("redis", 6379, timeouts = RedisTimeouts(command = Duration.ZERO))
        }
        assertFailsWith<IllegalArgumentException> {
            RedisSettings("redis", 6379, timeouts = RedisTimeouts(connect = Duration.ofMillis(-1)))
        }
    }
}
