package it.chalmers.gamma.platform.database

import com.zaxxer.hikari.HikariDataSource
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatabaseFactoryIntegrationTest {
    @Test
    fun `connects to a database migrated by its owner`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
            ).use { database ->
                assertFalse(database.isTableEmpty("g_user"))
            }

            val migrationVersions =
                postgres.connection { connection ->
                    connection
                        .prepareStatement(
                            "SELECT version FROM flyway_schema_history ORDER BY installed_rank",
                        ).use { statement ->
                            statement.executeQuery().use { result ->
                                buildList {
                                    while (result.next()) add(result.getString("version"))
                                }
                            }
                        }
                }
            assertEquals(listOf("1", "2", "3", "4", "5"), migrationVersions)
        }
    }

    @Test
    fun `database pool applies supported Hikari settings`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            val settings =
                DatabaseSettings(
                    postgres.jdbcUrl,
                    postgres.username,
                    postgres.password,
                    maximumPoolSize = 4,
                    minimumIdle = 1,
                    connectionTimeout = Duration.ofSeconds(12),
                    maximumLifetime = Duration.ofMinutes(12),
                    idleTimeout = Duration.ofMinutes(2),
                    keepaliveTime = Duration.ofMinutes(1),
                    validationTimeout = Duration.ofSeconds(2),
                    initializationFailTimeout = Duration.ofSeconds(2),
                    transactionIsolation = "TRANSACTION_SERIALIZABLE",
                )

            DatabaseFactory(settings).use { database ->
                val pool = database.dataSource as HikariDataSource
                assertEquals(4, pool.maximumPoolSize)
                assertEquals(1, pool.minimumIdle)
                assertEquals(12_000, pool.connectionTimeout)
                assertEquals(720_000, pool.maxLifetime)
                assertEquals(120_000, pool.idleTimeout)
                assertEquals(60_000, pool.keepaliveTime)
                assertEquals(2_000, pool.validationTimeout)
                assertEquals(2_000, pool.initializationFailTimeout)
                assertEquals("TRANSACTION_SERIALIZABLE", pool.transactionIsolation)
            }
        }
    }

    @Test
    fun `does not close a caller owned data source`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            DatabaseFactory(postgres.dataSource).close()

            assertTrue(postgres.connection { connection -> connection.isValid(1) })
        }
    }

    private fun migrationLocations(): List<String> =
        listOf(
            "filesystem:" +
                Path
                    .of(checkNotNull(System.getProperty("gamma.root")))
                    .resolve("app/src/main/resources/db/migration")
                    .toAbsolutePath(),
        )
}
