package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DeleteApiKeyIntegrationTest {
    @Test
    fun `deletion removes settings types managed flags and description while preserving other keys`() =
        withDatabase { database ->
            val keys = ApiKeyQueries(database)
            val otherId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
            val other =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, otherId)
                }
            database.commitTransaction {
                ReplaceApiKeySettings(database).replaceIn(this, scaffoldId, settings)
            }
            val tables =
                listOf(
                    "g_api_key",
                    "g_api_key_settings",
                    "g_api_key_to_super_group_type",
                    "g_api_key_account_scaffold_requires_managed",
                    "g_text",
                )
            val before = tables.map(database::tableRowCount)
            val operation = DeleteApiKey(database)
            database.commitTransaction { operation.deleteIn(this, scaffoldId) }
            assertNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, scaffoldId)
                },
            )
            assertNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.accountScaffoldSettingsIn(this, scaffoldId)
                },
            )
            assertEquals(
                other,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, otherId)
                },
            )
            assertEquals(
                before.zip(listOf(1L, 1L, 2L, 1L, 1L)) { count, removed -> count - removed },
                tables.map(database::tableRowCount),
            )
            val after = tables.map(database::tableRowCount)
            assertFailsWith<ApiAccessNotFound> { database.commitTransaction { operation.deleteIn(this, scaffoldId) } }
            assertEquals(after, tables.map(database::tableRowCount))
        }

    @Test
    fun `failure deleting the key rolls back earlier settings deletion`() =
        withDatabase { database ->
            val keys = ApiKeyQueries(database)
            database.commitTransaction { ReplaceApiKeySettings(database).replaceIn(this, scaffoldId, settings) }
            val beforeKey =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, scaffoldId)
                }
            val beforeSettings =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.accountScaffoldSettingsIn(this, scaffoldId)
                }
            val beforeDescriptions = database.tableRowCount("g_text")
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_key_deletion() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'key deletion rejected'; END $$;
                CREATE TRIGGER reject_key_deletion BEFORE DELETE ON g_api_key
                FOR EACH ROW EXECUTE FUNCTION reject_key_deletion();
                """.trimIndent(),
            )
            assertFailsWith<ExposedSQLException> {
                database.commitTransaction { DeleteApiKey(database).deleteIn(this, scaffoldId) }
            }
            assertEquals(
                beforeKey,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, scaffoldId)
                },
            )
            assertEquals(
                beforeSettings,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.accountScaffoldSettingsIn(this, scaffoldId)
                },
            )
            assertEquals(beforeDescriptions, database.tableRowCount("g_text"))
        }

    @Test
    fun `deletion participates in caller rollback and rejects invalid transaction handles`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val operation = DeleteApiKey(database)
                    val keys = ApiKeyQueries(database)
                    val previous =
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            keys.findApiKeyIn(this, scaffoldId)
                        }
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            operation.deleteIn(this, scaffoldId)
                            throw IllegalArgumentException("caller rejected deletion")
                        }
                    }
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> {
                            operation.deleteIn(
                                this,
                                scaffoldId,
                            )
                        }
                    }
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> { operation.deleteIn(completed, scaffoldId) }
                    assertEquals(
                        previous,
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            keys.findApiKeyIn(this, scaffoldId)
                        },
                    )
                }
            }
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val scaffoldId = ApiKeyId.parse("22222222-2222-4222-8222-222222222222")
        val settings =
            ApiKeyAccountScaffoldSettings(
                0,
                listOf(
                    SuperGroupTypeSetting(SuperGroupType("committee"), true),
                    SuperGroupTypeSetting(SuperGroupType("society"), false),
                ),
            )
    }
}
