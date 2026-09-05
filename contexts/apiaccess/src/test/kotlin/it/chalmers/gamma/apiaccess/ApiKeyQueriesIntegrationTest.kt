package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ApiKeyQueriesIntegrationTest {
    @Test
    fun `legacy null values use defaults and orphan settings do not appear as keys`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val id = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
                database.executeSqlScript(
                    """
                    UPDATE g_api_key SET description = NULL, version = NULL WHERE api_key_id = '${id.value}';
                    UPDATE g_api_key_settings SET version = NULL WHERE api_key_id = '${id.value}';
                    INSERT INTO g_api_key_settings (settings_id, created_at, updated_at, version, api_key_id)
                    VALUES (gen_random_uuid(), NOW(), NOW(), NULL, NULL);
                    """.trimIndent(),
                )
                val queries = ApiKeyQueries(database)
                database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
                    val key = assertNotNull(queries.findApiKeyIn(this, id))
                    assertEquals(LocalizedText.of(), key.description)
                    assertEquals(0, key.version)
                    assertEquals(ApiKeyType.INFO, key.type)
                    assertEquals(3, queries.listApiKeysIn(this).size)
                    assertEquals(0, assertNotNull(queries.infoSettingsIn(this, id)).version)
                }
            }
        }
    }

    @Test
    fun `settings queries reject keys of the wrong type`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val queries = ApiKeyQueries(database)
                database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
                    val wrongInfo =
                        assertFailsWith<RuntimeException> {
                            queries.infoSettingsIn(this, ApiKeyId.parse("22222222-2222-4222-8222-222222222222"))
                        }
                    val wrongScaffold =
                        assertFailsWith<RuntimeException> {
                            queries.accountScaffoldSettingsIn(
                                this,
                                ApiKeyId.parse("11111111-1111-4111-8111-111111111111"),
                            )
                        }
                    assertEquals("Unexpected api key type", wrongInfo.message)
                    assertEquals("Unexpected api key type", wrongScaffold.message)
                }
            }
        }
    }
}
