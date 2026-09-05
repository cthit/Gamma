package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReplaceApiKeySettingsIntegrationTest {
    @Test
    fun `legacy null settings version accepts zero once and then rejects a stale replacement`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                database.executeSqlScript(
                    "UPDATE g_api_key_settings SET version = NULL WHERE api_key_id = '${infoKey.value}'",
                )
                val operation = ReplaceApiKeySettings(database)
                database.commitTransaction { operation.replaceIn(this, infoKey, replacement) }
                val queries = ApiKeyQueries(database)
                val saved =
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        queries.infoSettingsIn(this, infoKey)
                    }
                assertEquals(replacement.copy(version = 1), saved)
                assertFailsWith<ApiAccessConflict> {
                    database.commitTransaction { operation.replaceIn(this, infoKey, replacement) }
                }
                assertEquals(
                    saved,
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        queries.infoSettingsIn(this, infoKey)
                    },
                )
            }
        }
    }

    @Test
    fun `settings participate in their callers rollback`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val operation = ReplaceApiKeySettings(database)
                val keys = ApiKeyQueries(database)
                val previous =
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.infoSettingsIn(this, infoKey)
                    }
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction {
                        operation.replaceIn(this, infoKey, replacement)
                        throw IllegalArgumentException("later participant rejected the operation")
                    }
                }
                assertEquals(
                    previous,
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.infoSettingsIn(this, infoKey)
                    },
                )
            }
        }
    }

    @Test
    fun `a participant rejects a foreign or completed transaction before writing`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val operation = ReplaceApiKeySettings(database)
                    val keys = ApiKeyQueries(database)
                    val previous =
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            keys.infoSettingsIn(this, infoKey)
                        }
                    lateinit var completed: JdbcTransaction
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> { operation.replaceIn(this, infoKey, replacement) }
                    }
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> { operation.replaceIn(completed, infoKey, replacement) }
                    database.commitTransaction {
                        assertFailsWith<IllegalStateException> { operation.replaceIn(completed, infoKey, replacement) }
                    }
                    assertEquals(
                        previous,
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            keys.infoSettingsIn(this, infoKey)
                        },
                    )
                }
            }
        }
    }

    private companion object {
        val infoKey = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
        val replacement = ApiKeyInfoSettings(0, listOf(SuperGroupType("society")))
    }
}
