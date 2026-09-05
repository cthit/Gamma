package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiAccessConflict
import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyAccountScaffoldSettings
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyInfoSettings
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ReplaceApiKeySettings
import it.chalmers.gamma.apiaccess.SuperGroupTypeSetting
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.AdministratorAccess
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateApiKeySettingsIntegrationTest {
    @Test
    fun `a cached administrator flag cannot authorize a settings update`() =
        withDatabase { database ->
            val keys = ApiKeyQueries(database)
            val previous =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.infoSettingsIn(this, infoKey)
                }
            val staleAdministrator =
                Actor.User(
                    ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")),
                    true,
                )
            val operation =
                UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
            assertFailsWith<AccessDenied> { operation.update(staleAdministrator, infoKey, newSettings) }
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

    @Test
    fun `settings update rejects an enclosing transaction`() =
        withDatabase { database ->
            val keys = ApiKeyQueries(database)
            val previous =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.infoSettingsIn(this, infoKey)
                }
            val operation =
                UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    operation.update(
                        administrator.copy(isAdministrator = true),
                        infoKey,
                        newSettings,
                    )
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

    @Test
    fun `current authority permits both settings types and replacement clears old managed flags`() =
        withDatabase { database ->
            val operation =
                UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
            val keys = ApiKeyQueries(database)
            operation.update(administrator, infoKey, newSettings)
            assertEquals(
                newSettings.copy(
                    version = 1,
                ),
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.infoSettingsIn(this, infoKey)
                },
            )
            val scaffold =
                ApiKeyAccountScaffoldSettings(
                    0,
                    listOf(
                        SuperGroupTypeSetting(SuperGroupType("society"), requiresManaged = true),
                        SuperGroupTypeSetting(SuperGroupType("committee"), requiresManaged = false),
                        SuperGroupTypeSetting(SuperGroupType("committee"), requiresManaged = true),
                    ),
                )
            operation.update(administrator, scaffoldKey, scaffold)
            val stored =
                assertNotNull(
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.accountScaffoldSettingsIn(this, scaffoldKey)
                    },
                )
            assertEquals(1, stored.version)
            assertEquals(listOf("committee", "society"), stored.superGroupTypes.map { it.type.value })
            assertEquals(listOf(false, true), stored.superGroupTypes.map { it.requiresManaged })
            val replacement =
                ApiKeyAccountScaffoldSettings(
                    1,
                    listOf(SuperGroupTypeSetting(SuperGroupType("society"), requiresManaged = false)),
                )
            operation.update(administrator, scaffoldKey, replacement)
            assertEquals(
                replacement.copy(
                    version = 2,
                ),
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.accountScaffoldSettingsIn(this, scaffoldKey)
                },
            )
        }

    @Test
    fun `stale versions and wrong types leave persisted settings unchanged`() =
        withDatabase { database ->
            val operation =
                UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
            val keys = ApiKeyQueries(database)
            operation.update(administrator, infoKey, newSettings)
            val stored =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.infoSettingsIn(this, infoKey)
                }
            assertFailsWith<ApiAccessConflict> {
                operation.update(administrator, infoKey, newSettings.copy(superGroupTypes = emptyList()))
            }
            val wrongType =
                assertFailsWith<RuntimeException> {
                    operation.update(administrator, infoKey, ApiKeyAccountScaffoldSettings(1, emptyList()))
                }
            assertEquals("Unexpected api key type", wrongType.message)
            assertEquals(
                stored,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.infoSettingsIn(this, infoKey)
                },
            )
        }

    @Test
    fun `missing keys and missing settings are reported after authorization`() =
        withDatabase { database ->
            val operation =
                UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
            val missing = ApiKeyId(UUID.randomUUID())
            assertFailsWith<AccessDenied> { operation.update(Actor.Anonymous, missing, newSettings) }
            assertFailsWith<ApiAccessNotFound> { operation.update(administrator, missing, newSettings) }
            database.executeSqlScript(
                "UPDATE g_api_key_settings SET api_key_id = NULL WHERE api_key_id = '${infoKey.value}'",
            )
            assertFailsWith<ApiAccessNotFound> { operation.update(administrator, infoKey, newSettings) }
        }

    @Test
    fun `database failure after replacing types rolls back the complete settings update`() =
        withDatabase { database ->
            val operation =
                UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
            val keys = ApiKeyQueries(database)
            val previous =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.accountScaffoldSettingsIn(this, scaffoldKey)
                }
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_settings_update() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'settings update rejected'; END $$;
                CREATE TRIGGER reject_settings_update BEFORE UPDATE ON g_api_key_settings
                FOR EACH ROW EXECUTE FUNCTION reject_settings_update();
                """.trimIndent(),
            )
            assertFailsWith<ExposedSQLException> {
                operation.update(
                    administrator,
                    scaffoldKey,
                    ApiKeyAccountScaffoldSettings(
                        0,
                        listOf(SuperGroupTypeSetting(SuperGroupType("society"), requiresManaged = true)),
                    ),
                )
            }
            assertEquals(
                previous,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.accountScaffoldSettingsIn(this, scaffoldKey)
                },
            )
        }

    @Test
    fun `competing edits have one committed winner and one stale version`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val operation =
                    UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
                val changes = listOf(newSettings, newSettings.copy(superGroupTypes = listOf(SuperGroupType("society"))))
                postgres.connection { blocker ->
                    blocker.createStatement().use {
                        it
                            .executeQuery(
                                "SELECT api_key_id FROM g_api_key WHERE api_key_id = '${infoKey.value}' FOR UPDATE",
                            ).close()
                    }
                    val workers = Executors.newFixedThreadPool(2)
                    try {
                        val edits =
                            changes.map { change ->
                                workers.submit<Boolean> {
                                    try {
                                        operation.update(administrator, infoKey, change)
                                        true
                                    } catch (_: ApiAccessConflict) {
                                        false
                                    }
                                }
                            }
                        waitForSettingsLocks(postgres, expected = 2)
                        blocker.commit()
                        val results = edits.map { it.get(20, TimeUnit.SECONDS) }
                        assertEquals(1, results.count { it })
                        assertEquals(
                            changes[results.indexOf(true)].copy(version = 1),
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                ApiKeyQueries(database).infoSettingsIn(this, infoKey)
                            },
                        )
                    } finally {
                        blocker.rollback()
                        workers.shutdownNow()
                    }
                }
            }
        }
    }

    @Test
    fun `administrator demotion waits for the authorized settings commit`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val operation =
                    UpdateApiKeySettings(database, AdministratorAccess(database), ReplaceApiKeySettings(database))
                postgres.connection { blocker ->
                    blocker.createStatement().use {
                        it
                            .executeQuery(
                                "SELECT api_key_id FROM g_api_key WHERE api_key_id = '${infoKey.value}' FOR UPDATE",
                            ).close()
                    }
                    val workers = Executors.newFixedThreadPool(2)
                    try {
                        val update =
                            workers.submit {
                                operation.update(
                                    administrator,
                                    infoKey,
                                    newSettings,
                                )
                            }
                        waitForSettingsLocks(postgres, expected = 1)
                        val demotion =
                            workers.submit {
                                postgres.connection { connection ->
                                    connection.createStatement().use {
                                        it.executeUpdate(
                                            "DELETE FROM g_admin_user WHERE user_id = '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f'",
                                        )
                                    }
                                    connection.commit()
                                }
                            }
                        waitForSettingsLocks(postgres, expected = 1, table = "g_admin_user")
                        blocker.commit()
                        update.get(20, TimeUnit.SECONDS)
                        demotion.get(20, TimeUnit.SECONDS)
                        assertEquals(
                            newSettings.copy(
                                version = 1,
                            ),
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                ApiKeyQueries(database).infoSettingsIn(this, infoKey)
                            },
                        )
                        assertFailsWith<AccessDenied> {
                            operation.update(administrator, infoKey, newSettings.copy(version = 1))
                        }
                    } finally {
                        blocker.rollback()
                        workers.shutdownNow()
                    }
                }
            }
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")))
        val infoKey = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
        val scaffoldKey = ApiKeyId.parse("22222222-2222-4222-8222-222222222222")
        val newSettings = ApiKeyInfoSettings(0, listOf(SuperGroupType("committee")))
    }
}

private fun waitForSettingsLocks(
    postgres: PostgresTestEnvironment,
    expected: Int,
    table: String = "g_api_key",
) {
    repeat(200) {
        val blocked =
            postgres.connection { connection ->
                connection.createStatement().use { statement ->
                    statement
                        .executeQuery(
                            "SELECT COUNT(*) FROM pg_stat_activity WHERE datname = current_database() " +
                                "AND cardinality(pg_blocking_pids(pid)) > 0 AND query LIKE '%$table%'",
                        ).use { result ->
                            check(result.next())
                            result.getInt(1)
                        }
                }
            }
        if (blocked >= expected) return
        Thread.sleep(25)
    }
    error("Timed out waiting for $expected settings updates to acquire their key lock")
}
