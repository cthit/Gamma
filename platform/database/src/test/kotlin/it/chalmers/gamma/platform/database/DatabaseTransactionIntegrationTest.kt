package it.chalmers.gamma.platform.database

import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DatabaseTransactionIntegrationTest {
    @Test
    fun `read only mode blocks writes on the first Exposed transaction`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            postgres.connection { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("CREATE TABLE gamma_read_only_test (value INTEGER NOT NULL)")
                }
                connection.commit()
            }

            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
            ).use { database ->
                assertFailsWith<SQLException> {
                    run {
                        database.commitTransaction(readOnly = true) {
                            exec("INSERT INTO gamma_read_only_test (value) VALUES (1)")
                        }
                    }
                }
                assertTrue(database.isTableEmpty("gamma_read_only_test"))

                run {
                    database.commitTransaction {
                        exec("INSERT INTO gamma_read_only_test (value) VALUES (1)")
                    }
                }
                assertFalse(database.isTableEmpty("gamma_read_only_test"))
            }
        }
    }

    @Test
    fun `transactions preserve the configured Hikari defaults`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            postgres.connection { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("CREATE TABLE gamma_read_only_test (value INTEGER NOT NULL)")
                }
                connection.commit()
            }

            val settings =
                DatabaseSettings(
                    postgres.jdbcUrl,
                    postgres.username,
                    postgres.password,
                    maximumPoolSize = 2,
                    readOnly = true,
                    transactionIsolation = "TRANSACTION_SERIALIZABLE",
                )
            DatabaseFactory(settings).use { database ->
                val transactionIsolation =
                    run {
                        database.commitTransaction {
                            exec("SHOW transaction_isolation") { result ->
                                check(result.next())
                                result.getString(1)
                            }
                        }
                    }

                assertEquals("serializable", transactionIsolation)
                assertFailsWith<SQLException> {
                    run {
                        database.commitTransaction {
                            exec("INSERT INTO gamma_read_only_test (value) VALUES (1)")
                        }
                    }
                }
                assertTrue(database.isTableEmpty("gamma_read_only_test"))
            }
        }
    }

    @Test
    fun `database boundary validates table names and preserves transaction rollback`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
            ).use { database ->
                assertFalse(database.isTableEmpty("g_user"))
                assertFailsWith<IllegalArgumentException> { database.isTableEmpty("g_user; DROP TABLE g_user") }

                database.executeSqlScript("CREATE TABLE gamma_boundary_test (value INTEGER NOT NULL)")
                assertTrue(database.isTableEmpty("gamma_boundary_test"))
                var invocations = 0
                val expectedFailure = IllegalStateException("force rollback")
                val actualFailure =
                    assertFailsWith<IllegalStateException> {
                        run {
                            database.commitTransaction {
                                invocations += 1
                                database.requireTransaction(this)
                                exec("INSERT INTO gamma_boundary_test (value) VALUES (1)")
                                throw expectedFailure
                            }
                        }
                    }
                assertSame(expectedFailure, actualFailure)
                assertEquals(1, invocations)
                assertTrue(database.isTableEmpty("gamma_boundary_test"))
                assertFailsWith<IllegalStateException> {
                    run {
                        database.commitTransaction {
                            exec("INSERT INTO gamma_boundary_test (value) VALUES (1)")
                            error("force rollback")
                        }
                    }
                }
                assertTrue(database.isTableEmpty("gamma_boundary_test"))
            }
        }
    }

    @Test
    fun `complete operation preserves database retries for SQL failures`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
            ).use { database ->
                database.executeSqlScript("CREATE TABLE gamma_complete_operation_test (value INTEGER NOT NULL)")
                var invocations = 0
                val expectedFailure = SQLException("force rollback")

                val actualFailure =
                    assertFailsWith<SQLException> {
                        run {
                            database.commitTransaction {
                                invocations += 1
                                database.requireTransaction(this)
                                exec("INSERT INTO gamma_complete_operation_test (value) VALUES (1)")
                                throw expectedFailure
                            }
                        }
                    }

                assertSame(expectedFailure, actualFailure)
                assertEquals(3, invocations)
                assertTrue(database.isTableEmpty("gamma_complete_operation_test"))
            }
        }
    }

    @Test
    fun `complete operation preserves application failure and rolls back`() {
        PostgresTestEnvironment(migrationLocations()).use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
            ).use { database ->
                database.executeSqlScript("CREATE TABLE gamma_complete_operation_test (value INTEGER NOT NULL)")
                var invocations = 0
                val expectedFailure = IllegalStateException("operation failed")

                val actualFailure =
                    assertFailsWith<IllegalStateException> {
                        run {
                            database.commitTransaction {
                                invocations += 1
                                database.requireTransaction(this)
                                exec("INSERT INTO gamma_complete_operation_test (value) VALUES (1)")
                                throw expectedFailure
                            }
                        }
                    }

                assertSame(expectedFailure, actualFailure)
                assertEquals(1, invocations)
                assertTrue(database.isTableEmpty("gamma_complete_operation_test"))
            }
        }
    }

    private fun migrationLocations(): List<String> = listOf("filesystem:${migrationDirectory().toAbsolutePath()}")

    private fun migrationDirectory(): Path =
        Path
            .of(checkNotNull(System.getProperty("gamma.root")))
            .resolve("app/src/main/resources/db/migration")
}
