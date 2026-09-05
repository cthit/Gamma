package it.chalmers.gamma.platform.database

import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseParticipationIntegrationTest {
    @Test
    fun `participants write in their callers transaction and roll back together`() {
        PostgresTestEnvironment(loadRegressionFixture = false).use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                database.executeSqlScript("CREATE TABLE participant_test (value INTEGER NOT NULL)")
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction {
                        database.requireTransaction(this)
                        exec("INSERT INTO participant_test VALUES (1)")
                        throw IllegalArgumentException("caller rejected operation")
                    }
                }
                assertEquals(0, database.tableRowCount("participant_test"))
                database.commitTransaction {
                    database.requireTransaction(this)
                    exec("INSERT INTO participant_test VALUES (2)")
                }
                assertEquals(1, database.tableRowCount("participant_test"))
            }
        }
    }

    @Test
    fun `completed transactions and transactions belonging to another factory are rejected`() {
        PostgresTestEnvironment(loadRegressionFixture = false).use { postgres ->
            DatabaseFactory(postgres.dataSource).use { first ->
                DatabaseFactory(postgres.dataSource).use { second ->
                    lateinit var completed: JdbcTransaction
                    first.commitTransaction {
                        completed = this
                        assertFailsWith<IllegalStateException> { second.requireTransaction(this) }
                    }
                    assertFailsWith<IllegalStateException> { first.requireTransaction(completed) }
                    first.commitTransaction {
                        assertFailsWith<IllegalStateException> { first.requireTransaction(completed) }
                    }
                }
            }
        }
    }
}
