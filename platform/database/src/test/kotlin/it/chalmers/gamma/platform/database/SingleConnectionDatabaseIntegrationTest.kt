package it.chalmers.gamma.platform.database

import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.time.Duration
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SingleConnectionDatabaseIntegrationTest {
    @Test
    fun `startup validation preserves a configured one connection application pool`() {
        val migrations =
            Path
                .of(checkNotNull(System.getProperty("gamma.root")))
                .resolve("app/src/main/resources/db/migration")
        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}")).use { postgres ->
            DatabaseFactory(
                DatabaseSettings(
                    jdbcUrl = postgres.jdbcUrl,
                    username = postgres.username,
                    password = postgres.password,
                    maximumPoolSize = 1,
                    minimumIdle = 0,
                    connectionTimeout = Duration.ofMillis(500),
                ),
            ).use { database ->
                assertTrue(run { database.ping() })
            }
        }
    }

    @Test
    fun `database pool applies PostgreSQL driver properties`() {
        val migrationLocations = migrationLocations()
        PostgresTestEnvironment(migrationLocations, loadRegressionFixture = false).use { postgres ->
            val applicationName = "gamma-data-source-properties-test"
            val settings =
                DatabaseSettings(
                    postgres.jdbcUrl,
                    postgres.username,
                    postgres.password,
                    maximumPoolSize = 2,
                    dataSourceProperties = mapOf("ApplicationName" to applicationName),
                )

            DatabaseFactory(settings).use { database ->
                assertEquals(applicationName, database.dataSource.postgresApplicationName())
            }
        }
    }

    private fun migrationLocations(): List<String> {
        val migrations =
            Path
                .of(checkNotNull(System.getProperty("gamma.root")))
                .resolve("app/src/main/resources/db/migration")
                .toAbsolutePath()
        return listOf("filesystem:$migrations")
    }
}

private fun DataSource.postgresApplicationName(): String =
    connection.use { connection ->
        connection.createStatement().use { statement ->
            statement.executeQuery("SHOW application_name").use { result ->
                check(result.next()) { "PostgreSQL did not return its application_name" }
                result.getString(1)
            }
        }
    }
