package it.chalmers.gamma.platform.database

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatabaseSettingsTest {
    @Test
    fun `accepts a PostgreSQL JDBC URL`() {
        val settings =
            DatabaseSettings(
                jdbcUrl = "jdbc:postgresql://db:5432/gamma",
                username = "gamma",
                password = "secret",
                maximumPoolSize = 7,
            )

        assertEquals(7, settings.maximumPoolSize)
        assertEquals(7, settings.minimumIdle)
        assertEquals(Duration.ofSeconds(30), settings.connectionTimeout)
        assertEquals(Duration.ofMinutes(30), settings.maximumLifetime)
        assertEquals(Duration.ofMinutes(10), settings.idleTimeout)
        assertEquals(Duration.ZERO, settings.keepaliveTime)
        assertEquals(Duration.ofSeconds(5), settings.validationTimeout)
        assertEquals(Duration.ofMillis(1), settings.initializationFailTimeout)
        assertFalse(settings.readOnly)
        assertEquals(true, settings.autoCommit)
        assertEquals("TRANSACTION_READ_COMMITTED", settings.transactionIsolation)
        assertEquals(emptyMap(), settings.dataSourceProperties)
        assertEquals(
            "jdbc:postgresql://db-a:5432,db-b:5433/gamma",
            DatabaseSettings("jdbc:postgresql://db-a:5432,db-b:5433/gamma", "gamma", "secret").jdbcUrl,
        )
        assertEquals(
            "jdbc:postgresql:gamma",
            DatabaseSettings("jdbc:postgresql:gamma", "gamma", "secret").jdbcUrl,
        )
        assertEquals(
            "jdbc:postgresql://db/gamma?user=legacy&password=legacy-secret",
            DatabaseSettings(
                "jdbc:postgresql://db/gamma?user=legacy&password=legacy-secret",
                "gamma",
                "secret",
            ).jdbcUrl,
        )
    }

    @Test
    fun `rejects a non PostgreSQL database`() {
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings(
                jdbcUrl = "jdbc:h2:mem:test",
                username = "gamma",
                password = "secret",
            )
        }
        assertFailsWith<IllegalArgumentException> { DatabaseSettings("jdbc:postgresql:", "gamma", "secret") }
        assertFailsWith<IllegalArgumentException> { DatabaseSettings("jdbc:postgresql://", "gamma", "secret") }
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings("jdbc:postgresql://db-a,,db-b/gamma", "gamma", "secret")
        }
    }

    @Test
    fun `allows legacy trust-authentication values and rejects non positive pool sizes`() {
        assertEquals(" ", DatabaseSettings("jdbc:postgresql://db/gamma", " ", "secret").username)
        assertEquals("", DatabaseSettings("jdbc:postgresql://db/gamma", "gamma", "").password)
        assertEquals(" ", DatabaseSettings("jdbc:postgresql://db/gamma", "gamma", " ").password)
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings("jdbc:postgresql://db/gamma", "gamma", "secret", maximumPoolSize = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings(
                "jdbc:postgresql://db/gamma",
                "gamma",
                "secret",
                maximumPoolSize = 2,
                minimumIdle = 3,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings(
                "jdbc:postgresql://db/gamma",
                "gamma",
                "secret",
                connectionTimeout = Duration.ofMillis(249),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings(
                "jdbc:postgresql://db/gamma",
                "gamma",
                "secret",
                maximumLifetime = Duration.ofSeconds(29),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings(
                "jdbc:postgresql://db/gamma",
                "gamma",
                "secret",
                keepaliveTime = Duration.ofSeconds(29),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            DatabaseSettings(
                "jdbc:postgresql://db/gamma",
                "gamma",
                "secret",
                validationTimeout = Duration.ofMillis(249),
            )
        }
    }

    @Test
    fun `diagnostics retain connection identity without leaking credentials or query parameters`() {
        val usernameSentinel = "database-user-sentinel"
        val passwordSentinel = "database-password-sentinel"
        val settings =
            DatabaseSettings(
                "jdbc:postgresql://db/gamma?sslmode=verify-full&sslpassword=query-secret",
                usernameSentinel,
                passwordSentinel,
                3,
            )
        val rendered = settings.toString()

        assertEquals(true, rendered.contains("jdbc:postgresql://db/gamma"))
        assertEquals(true, rendered.contains("username=<redacted>"))
        assertEquals(true, rendered.contains("?<redacted>"))
        assertFalse(rendered.contains(usernameSentinel), "Database diagnostics must redact the username")
        assertFalse(rendered.contains(passwordSentinel), "Database diagnostics must redact the password")
        assertFalse(rendered.contains("query-secret"), "Database diagnostics must redact query values")
        assertFalse(rendered.contains("sslpassword"), "Database diagnostics must redact query parameter names")

        val embeddedCredentials =
            DatabaseSettings(
                "jdbc:postgresql://legacy-user:legacy-secret@db/gamma",
                "gamma",
                "top-secret",
            ).toString()
        assertFalse(embeddedCredentials.contains("legacy-user"))
        assertFalse(embeddedCredentials.contains("legacy-secret"))

        val driverSecret = "driver-property-secret"
        val driverSettings =
            DatabaseSettings(
                "jdbc:postgresql://db/gamma",
                "gamma",
                "top-secret",
                dataSourceProperties = mapOf("sslpassword" to driverSecret),
            ).toString()
        assertTrue(driverSettings.contains("dataSourceProperties=1"))
        assertFalse(driverSettings.contains("sslpassword"))
        assertFalse(driverSettings.contains(driverSecret))
    }
}
