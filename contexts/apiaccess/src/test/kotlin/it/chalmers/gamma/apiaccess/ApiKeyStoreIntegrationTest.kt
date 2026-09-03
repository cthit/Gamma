package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApiKeyStoreIntegrationTest {
    @Test
    fun `reads and updates nullable API key values`() {
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
                        UPDATE g_api_key SET description = NULL, version = NULL
                        WHERE api_key_id = '${apiKeyId.value}';
                        UPDATE g_api_key_settings SET version = NULL
                        WHERE api_key_id = '${apiKeyId.value}';
                        INSERT INTO g_api_key_settings (
                            settings_id, created_at, updated_at, version, api_key_id
                        ) VALUES (
                            '40000000-0000-4000-8000-000000000099', NOW(), NOW(), NULL, NULL
                        )
                        """.trimIndent(),
                    )
                    val api = ApiKeyStore(database, bcryptCost = 10)

                    run {
                        assertNotNull(
                            api.authenticate(
                                ApiKeyType.INFO,
                                apiKeyId,
                                RawApiToken("gamma-info-regression-token-000001"),
                            ),
                        )
                        val storedKey = assertNotNull(api.findApiKey(apiKeyId))
                        assertEquals(LocalizedText.of(), storedKey.description)
                        assertEquals(0, storedKey.version)
                        assertEquals(3, api.listApiKeys().size)

                        val replacement = api.resetToken(apiKeyId)
                        assertNotNull(api.authenticate(ApiKeyType.INFO, apiKeyId, replacement))
                        assertEquals(1, api.findApiKey(apiKeyId)?.version)

                        val settings = assertNotNull(api.infoSettings(apiKeyId))
                        assertEquals(0, settings.version)
                        val wrongRead =
                            assertFailsWith<RuntimeException> { api.accountScaffoldSettings(apiKeyId) }
                        assertEquals("Unexpected api key type", wrongRead.message)
                        val wrongWrite =
                            assertFailsWith<RuntimeException> {
                                api.updateAccountScaffoldSettings(
                                    apiKeyId,
                                    ApiKeyAccountScaffoldSettings(0, emptyList()),
                                )
                            }
                        assertEquals("Unexpected api key type", wrongWrite.message)
                        assertEquals(settings, api.infoSettings(apiKeyId))
                        api.updateInfoSettings(apiKeyId, settings)
                        assertEquals(1, api.infoSettings(apiKeyId)?.version)
                    }
                }
            }
    }

    @Test
    fun `creates authenticates configures resets and deletes keys on real postgres`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(
                        jdbcUrl = postgres.jdbcUrl,
                        username = postgres.username,
                        password = postgres.password,
                        maximumPoolSize = 2,
                    ),
                ).use { database ->
                    val verificationCache = RecordingApiTokenVerificationCache()
                    val api = ApiKeyStore(database, verificationCache, bcryptCost = 10)

                    run {
                        assertEquals(3, api.listApiKeys().size)
                        assertEquals(
                            ApiKeyType.INFO,
                            api.findApiKey(ApiKeyId.parse("11111111-1111-4111-8111-111111111111"))?.type,
                        )

                        val created =
                            api.createApiKey(
                                ApiKeyName("account-regression"),
                                LocalizedText.of("Regressionsnyckel", "Regression key"),
                                ApiKeyType.ACCOUNT_SCAFFOLD,
                            )
                        assertNotEquals(created.token.value, created.token.toString())
                        assertEquals(
                            created.apiKey,
                            api.authenticate(ApiKeyType.ACCOUNT_SCAFFOLD, created.apiKey.id, created.token),
                        )
                        assertNull(
                            api.authenticate(
                                ApiKeyType.ACCOUNT_SCAFFOLD,
                                created.apiKey.id,
                                RawApiToken("wrong-token-value-that-is-definitely-long-enough"),
                            ),
                        )
                        assertEquals(2, verificationCache.matchAttempts)
                        assertEquals(1, verificationCache.rememberedCredentialCount)

                        val initialSettings = assertNotNull(api.accountScaffoldSettings(created.apiKey.id))
                        assertEquals(0, initialSettings.version)
                        assertEquals(emptyList(), initialSettings.superGroupTypes)
                        val wrongRead =
                            assertFailsWith<RuntimeException> { api.infoSettings(created.apiKey.id) }
                        assertEquals("Unexpected api key type", wrongRead.message)
                        val wrongWrite =
                            assertFailsWith<RuntimeException> {
                                api.updateInfoSettings(
                                    created.apiKey.id,
                                    ApiKeyInfoSettings(0, listOf(SuperGroupType("committee"))),
                                )
                            }
                        assertEquals("Unexpected api key type", wrongWrite.message)
                        assertEquals(initialSettings, api.accountScaffoldSettings(created.apiKey.id))
                        api.updateAccountScaffoldSettings(
                            created.apiKey.id,
                            initialSettings.copy(
                                superGroupTypes =
                                    listOf(
                                        SuperGroupTypeSetting(SuperGroupType("committee"), requiresManaged = true),
                                        SuperGroupTypeSetting(SuperGroupType("society"), requiresManaged = false),
                                    ),
                            ),
                        )
                        val updatedSettings = assertNotNull(api.accountScaffoldSettings(created.apiKey.id))
                        assertEquals(1, updatedSettings.version)
                        assertEquals(
                            listOf("committee", "society"),
                            updatedSettings.superGroupTypes.map { it.type.value },
                        )
                        assertEquals(listOf(true, false), updatedSettings.superGroupTypes.map { it.requiresManaged })
                        assertFailsWith<ApiAccessConflict> {
                            api.updateAccountScaffoldSettings(created.apiKey.id, initialSettings)
                        }

                        val replacement = api.resetToken(created.apiKey.id)
                        assertNull(api.authenticate(ApiKeyType.ACCOUNT_SCAFFOLD, created.apiKey.id, created.token))
                        assertNotNull(api.authenticate(ApiKeyType.ACCOUNT_SCAFFOLD, created.apiKey.id, replacement))

                        api.deleteApiKey(created.apiKey.id)
                        assertNull(api.findApiKey(created.apiKey.id))
                        assertNull(api.accountScaffoldSettings(created.apiKey.id))
                        assertFailsWith<ApiAccessNotFound> { api.resetToken(created.apiKey.id) }
                    }
                }
            }
    }
}

private class RecordingApiTokenVerificationCache : ApiTokenVerificationCache {
    private val tokens = mutableMapOf<Pair<ApiKeyId, StoredApiCredential>, String>()
    var matchAttempts = 0
        private set

    val rememberedCredentialCount: Int
        get() = tokens.size

    override fun match(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        presentedToken: RawApiToken,
    ): CachedApiTokenMatch {
        matchAttempts += 1
        val expected = tokens[id to storedCredential] ?: return CachedApiTokenMatch.MISS
        return if (expected == presentedToken.value) CachedApiTokenMatch.MATCH else CachedApiTokenMatch.MISMATCH
    }

    override fun remember(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    ) {
        tokens[id to storedCredential] = token.value
    }
}
