package it.chalmers.gamma.platform.database

import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DatabaseUnitOfWorkIntegrationTest {
    @Test
    fun `unit of work returns only the value from the committed attempt`() {
        withDatabase { database ->
            var attempts = 0

            val committedValue =
                run {
                    databaseUnitOfWork(database).run {
                        attempts += 1
                        if (attempts < 3) throw SQLException("retry attempt $attempts")
                        "committed-attempt-$attempts"
                    }
                }

            assertEquals(3, attempts)
            assertEquals("committed-attempt-3", committedValue)
        }
    }

    @Test
    fun `unit of work preserves application failures without retrying`() {
        withDatabase { database ->
            var attempts = 0
            val expectedFailure = IllegalStateException("operation failed")

            val actual =
                assertFailsWith<IllegalStateException> {
                    run {
                        databaseUnitOfWork(database).run {
                            attempts += 1
                            throw expectedFailure
                        }
                    }
                }

            assertSame(expectedFailure, actual)
            assertEquals(1, attempts)
        }
    }

    @Test
    fun `terminal transaction failure remains the caller's failure`() {
        withDatabase { database ->
            var attempts = 0
            val terminalFailure = SQLException("transaction failed")

            val actual =
                assertFailsWith<SQLException> {
                    run {
                        databaseUnitOfWork(database).run {
                            attempts += 1
                            throw terminalFailure
                        }
                    }
                }

            assertSame(terminalFailure, actual)
            assertEquals(3, attempts)
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment(migrationLocations(), loadRegressionFixture = false).use { postgres ->
            database(postgres).use(test)
        }
    }

    private fun database(postgres: PostgresTestEnvironment): DatabaseFactory =
        DatabaseFactory(
            DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
        )

    private fun migrationLocations(): List<String> = listOf("filesystem:${migrationDirectory().toAbsolutePath()}")

    private fun migrationDirectory(): Path =
        Path
            .of(checkNotNull(System.getProperty("gamma.root")))
            .resolve("app/src/main/resources/db/migration")
}
