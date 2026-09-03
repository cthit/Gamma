package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiKeyStoreSettingsInvariantIntegrationTest {
    @Test
    fun `duplicate settings fail before any read write or delete mutation`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")
        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
                ).use { database ->
                    val apiKeyId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
                    database.executeSqlScript(
                        """
                        INSERT INTO g_api_key_settings (
                            settings_id, created_at, updated_at, version, api_key_id
                        ) VALUES
                            ('40000000-0000-4000-8000-000000000090', NOW(), NOW(), 7,
                             '${apiKeyId.value}'),
                            ('40000000-0000-4000-8000-000000000091', NOW(), NOW(), 9, NULL);
                        INSERT INTO g_api_key_to_super_group_type (
                            settings_id, created_at, super_group_type_name
                        ) VALUES (
                            '40000000-0000-4000-8000-000000000090', NOW(), 'committee'
                        );
                        """.trimIndent(),
                    )
                    val api = ApiKeyStore(database, bcryptCost = 10)
                    val before = postgres.connection(::settingsState)

                    run {
                        assertDuplicateSettingsFailure { api.infoSettings(apiKeyId) }
                        assertEquals(before, postgres.connection(::settingsState))

                        assertDuplicateSettingsFailure {
                            api.updateInfoSettings(
                                apiKeyId,
                                ApiKeyInfoSettings(0, listOf(SuperGroupType("society"))),
                            )
                        }
                        assertEquals(before, postgres.connection(::settingsState))

                        assertDuplicateSettingsFailure { api.deleteApiKey(apiKeyId) }
                        assertEquals(before, postgres.connection(::settingsState))
                    }
                }
            }
    }

    private fun assertDuplicateSettingsFailure(operation: () -> Unit) {
        val failure = assertFailsWith<IllegalStateException> { operation() }
        assertEquals("Multiple API key settings rows exist for one API key", failure.message)
    }
}

private fun settingsState(connection: Connection): List<String> =
    listOf(
        connection.rows(
            "SELECT api_key_id, token, version, description FROM g_api_key " +
                "WHERE api_key_id = '11111111-1111-4111-8111-111111111111'",
        ),
        connection.rows(
            "SELECT settings_id, version, api_key_id FROM g_api_key_settings " +
                "WHERE api_key_id = '11111111-1111-4111-8111-111111111111' OR " +
                "settings_id = '40000000-0000-4000-8000-000000000091' ORDER BY settings_id",
        ),
        connection.rows(
            "SELECT settings_id, super_group_type_name FROM g_api_key_to_super_group_type " +
                "WHERE settings_id IN ('40000000-0000-4000-8000-000000000001', " +
                "'40000000-0000-4000-8000-000000000090') ORDER BY settings_id, super_group_type_name",
        ),
        connection.rows(
            "SELECT settings_id, super_group_type_name FROM g_api_key_account_scaffold_requires_managed " +
                "WHERE settings_id IN ('40000000-0000-4000-8000-000000000001', " +
                "'40000000-0000-4000-8000-000000000090') ORDER BY settings_id, super_group_type_name",
        ),
        connection.rows(
            "SELECT text_id, sv, en FROM g_text WHERE text_id = '30000000-0000-0000-0000-000000000001'",
        ),
    ).flatten()

private fun Connection.rows(sql: String): List<String> =
    prepareStatement(sql).use { statement ->
        statement.executeQuery().use { result ->
            val columnCount = result.metaData.columnCount
            buildList {
                while (result.next()) {
                    add((1..columnCount).joinToString("|") { column -> result.getString(column) ?: "<null>" })
                }
            }
        }
    }
