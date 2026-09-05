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

class ApiAccessOperationsIntegrationTest {
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
                    val api = ApiKeyQueries(database)
                    val authenticator = ApiCredentialAuthenticator(database)

                    run {
                        assertNotNull(
                            authenticator.authenticate(
                                apiKeyId,
                                RawApiToken("gamma-info-regression-token-000001"),
                                requiredType = ApiKeyType.INFO,
                            ),
                        )
                        val storedKey =
                            assertNotNull(
                                database.commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.findApiKeyIn(this, apiKeyId)
                                },
                            )
                        assertEquals(LocalizedText.of(), storedKey.description)
                        assertEquals(0, storedKey.version)
                        assertEquals(
                            3,
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.listApiKeysIn(this)
                                }.size,
                        )

                        val replacement =
                            RotateApiKey(
                                database,
                                bcryptCost = 10,
                            ).rotateForTest(database, apiKeyId).token
                        assertNotNull(authenticator.authenticate(apiKeyId, replacement, requiredType = ApiKeyType.INFO))
                        assertEquals(
                            1,
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.findApiKeyIn(this, apiKeyId)
                                }?.version,
                        )

                        val settings =
                            assertNotNull(
                                database.commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.infoSettingsIn(this, apiKeyId)
                                },
                            )
                        assertEquals(0, settings.version)
                        val wrongRead =
                            assertFailsWith<RuntimeException> {
                                database.commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.accountScaffoldSettingsIn(this, apiKeyId)
                                }
                            }
                        assertEquals("Unexpected api key type", wrongRead.message)
                        val wrongWrite =
                            assertFailsWith<RuntimeException> {
                                database.commitTransaction {
                                    ReplaceApiKeySettings(database).replaceIn(
                                        this,
                                        apiKeyId,
                                        ApiKeyAccountScaffoldSettings(0, emptyList()),
                                    )
                                }
                            }
                        assertEquals("Unexpected api key type", wrongWrite.message)
                        assertEquals(
                            settings,
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                api.infoSettingsIn(this, apiKeyId)
                            },
                        )
                        database.commitTransaction {
                            ReplaceApiKeySettings(
                                database,
                            ).replaceIn(this, apiKeyId, settings)
                        }
                        assertEquals(
                            1,
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.infoSettingsIn(this, apiKeyId)
                                }?.version,
                        )
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
                    val api = ApiKeyQueries(database)
                    val authenticator = ApiCredentialAuthenticator(database, verificationCache)

                    run {
                        assertEquals(
                            3,
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.listApiKeysIn(this)
                                }.size,
                        )
                        assertEquals(
                            ApiKeyType.INFO,
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.findApiKeyIn(this, ApiKeyId.parse("11111111-1111-4111-8111-111111111111"))
                                }?.type,
                        )

                        val created =
                            CreateApiKey(database, verificationCache, bcryptCost = 10).create(
                                ApiKeyName("account-regression"),
                                LocalizedText.of("Regressionsnyckel", "Regression key"),
                                ApiKeyType.ACCOUNT_SCAFFOLD,
                            )
                        assertNotEquals(created.token.value, created.token.toString())
                        assertEquals(
                            created.apiKey,
                            authenticator.authenticate(
                                created.apiKey.id,
                                created.token,
                                requiredType = ApiKeyType.ACCOUNT_SCAFFOLD,
                            ),
                        )
                        assertNull(
                            authenticator.authenticate(
                                created.apiKey.id,
                                RawApiToken("wrong-token-value-that-is-definitely-long-enough"),
                                requiredType = ApiKeyType.ACCOUNT_SCAFFOLD,
                            ),
                        )
                        assertEquals(2, verificationCache.matchAttempts)
                        assertEquals(1, verificationCache.rememberedCredentialCount)

                        val initialSettings =
                            assertNotNull(
                                database.commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.accountScaffoldSettingsIn(this, created.apiKey.id)
                                },
                            )
                        assertEquals(0, initialSettings.version)
                        assertEquals(emptyList(), initialSettings.superGroupTypes)
                        val wrongRead =
                            assertFailsWith<RuntimeException> {
                                database.commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.infoSettingsIn(this, created.apiKey.id)
                                }
                            }
                        assertEquals("Unexpected api key type", wrongRead.message)
                        val wrongWrite =
                            assertFailsWith<RuntimeException> {
                                database.commitTransaction {
                                    ReplaceApiKeySettings(database).replaceIn(
                                        this,
                                        created.apiKey.id,
                                        ApiKeyInfoSettings(0, listOf(SuperGroupType("committee"))),
                                    )
                                }
                            }
                        assertEquals("Unexpected api key type", wrongWrite.message)
                        assertEquals(
                            initialSettings,
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                api.accountScaffoldSettingsIn(this, created.apiKey.id)
                            },
                        )
                        database.commitTransaction {
                            ReplaceApiKeySettings(database).replaceIn(
                                this,
                                created.apiKey.id,
                                initialSettings.copy(
                                    superGroupTypes =
                                        listOf(
                                            SuperGroupTypeSetting(SuperGroupType("committee"), requiresManaged = true),
                                            SuperGroupTypeSetting(SuperGroupType("society"), requiresManaged = false),
                                        ),
                                ),
                            )
                        }
                        val updatedSettings =
                            assertNotNull(
                                database.commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    api.accountScaffoldSettingsIn(this, created.apiKey.id)
                                },
                            )
                        assertEquals(1, updatedSettings.version)
                        assertEquals(
                            listOf("committee", "society"),
                            updatedSettings.superGroupTypes.map { it.type.value },
                        )
                        assertEquals(listOf(true, false), updatedSettings.superGroupTypes.map { it.requiresManaged })
                        assertFailsWith<ApiAccessConflict> {
                            database.commitTransaction {
                                ReplaceApiKeySettings(
                                    database,
                                ).replaceIn(this, created.apiKey.id, initialSettings)
                            }
                        }

                        val replacement =
                            RotateApiKey(
                                database,
                                verificationCache,
                                bcryptCost = 10,
                            ).rotateForTest(database, created.apiKey.id).token
                        assertNull(
                            authenticator.authenticate(
                                created.apiKey.id,
                                created.token,
                                requiredType = ApiKeyType.ACCOUNT_SCAFFOLD,
                            ),
                        )
                        assertNotNull(
                            authenticator.authenticate(
                                created.apiKey.id,
                                replacement,
                                requiredType = ApiKeyType.ACCOUNT_SCAFFOLD,
                            ),
                        )

                        database.commitTransaction { DeleteApiKey(database).deleteIn(this, created.apiKey.id) }
                        assertNull(
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                api.findApiKeyIn(this, created.apiKey.id)
                            },
                        )
                        assertNull(
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                api.accountScaffoldSettingsIn(this, created.apiKey.id)
                            },
                        )
                        assertFailsWith<ApiAccessNotFound> {
                            RotateApiKey(
                                database,
                                verificationCache,
                                bcryptCost = 10,
                            ).rotateForTest(database, created.apiKey.id).token
                        }
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
