package it.chalmers.gamma.platform.database

import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DatabaseCommitIntegrationTest {
    @Test
    fun `operation returns after its writes have committed`() {
        PostgresTestEnvironment(loadRegressionFixture = false).use { postgres ->
            var committed = false
            val observer =
                object : StatementInterceptor {
                    override fun afterCommit(transaction: Transaction) {
                        committed = true
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(observer)).use { database ->
                database.executeSqlScript("CREATE TABLE operation_test (value INTEGER NOT NULL)")

                val result =
                    database.commitTransaction {
                        exec("INSERT INTO operation_test (value) VALUES (7)")
                        assertFalse(committed)
                        "saved"
                    }

                assertEquals("saved", result)
                assertTrue(committed)
                assertEquals(1, database.tableRowCount("operation_test"))
            }
        }
    }

    @Test
    fun `SQL retries return only the committed attempt and leave no failed writes`() {
        withDatabase { database ->
            var attempts = 0

            val committedAttempt =
                database.commitTransaction {
                    attempts += 1
                    exec("INSERT INTO operation_test (value) VALUES ($attempts)")
                    if (attempts < 3) throw SQLException("retry operation")
                    attempts
                }

            assertEquals(3, committedAttempt)
            assertEquals(1, database.tableRowCount("operation_test"))
            val persistedAttempt =
                database.commitTransaction(readOnly = true) {
                    exec("SELECT value FROM operation_test") { result ->
                        check(result.next())
                        result.getInt(1)
                    }
                }
            assertEquals(3, persistedAttempt)
        }
    }

    @Test
    fun `application failure rolls back without retrying`() {
        withDatabase { database ->
            val rejection = IllegalArgumentException("operation rejected")
            var attempts = 0

            val failure =
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction {
                        attempts += 1
                        exec("INSERT INTO operation_test (value) VALUES (1)")
                        throw rejection
                    }
                }

            assertSame(rejection, failure)
            assertEquals(1, attempts)
            assertTrue(database.isTableEmpty("operation_test"))
        }
    }

    @Test
    fun `nested complete operation is rejected before executing and cannot roll back its caller`() {
        withDatabase { database ->
            var nestedOperationRan = false

            database.commitTransaction {
                exec("INSERT INTO operation_test (value) VALUES (1)")
                assertFailsWith<IllegalStateException> {
                    database.commitTransaction {
                        nestedOperationRan = true
                        exec("INSERT INTO operation_test (value) VALUES (2)")
                    }
                }
            }

            assertFalse(nestedOperationRan)
            assertEquals(1, database.tableRowCount("operation_test"))
        }
    }

    @Test
    fun `terminal SQL failure is preserved after all attempts roll back`() {
        withDatabase { database ->
            val terminalFailure = SQLException("transaction failed")
            var attempts = 0

            val failure =
                assertFailsWith<SQLException> {
                    database.commitTransaction {
                        attempts += 1
                        exec("INSERT INTO operation_test (value) VALUES ($attempts)")
                        throw terminalFailure
                    }
                }

            assertSame(terminalFailure, failure)
            assertEquals(3, attempts)
            assertTrue(database.isTableEmpty("operation_test"))
        }
    }

    private fun withDatabase(operation: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment(loadRegressionFixture = false).use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                database.executeSqlScript("CREATE TABLE operation_test (value INTEGER NOT NULL)")
                operation(database)
            }
        }
    }
}
