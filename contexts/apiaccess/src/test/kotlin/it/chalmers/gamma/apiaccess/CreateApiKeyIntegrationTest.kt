package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.sql.SQLException
import java.util.concurrent.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CreateApiKeyIntegrationTest {
    @Test
    fun `an enclosing transaction is rejected before credential preparation`() =
        withDatabase { database ->
            var generations = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        generations++
                        bytes.fill(1)
                    }
                }
            val creation = CreateApiKey(database, bcryptCost = 10, random = random)
            val before = database.tableRowCount("g_api_key")
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    creation.create(ApiKeyName("ambient creation"), LocalizedText.of(), ApiKeyType.INFO)
                }
            }
            assertEquals(0, generations)
            assertEquals(before, database.tableRowCount("g_api_key"))
        }

    @Test
    fun `cache cancellation propagates after the key commits`() =
        withDatabase { database ->
            val cancellation = CancellationException("cache cancelled")
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ): Unit = throw cancellation
                }
            val before = database.tableRowCount("g_api_key")
            assertSame(
                cancellation,
                assertFailsWith<CancellationException> {
                    CreateApiKey(database, cache, bcryptCost = 10)
                        .create(ApiKeyName("cache cancellation"), LocalizedText.of(), ApiKeyType.INFO)
                },
            )
            assertEquals(before + 1, database.tableRowCount("g_api_key"))
        }

    @Test
    fun `each key type stores an authenticating credential and publishes only committed data`() =
        withDatabase { database ->
            val keys = ApiKeyQueries(database)
            var generations = 0
            var publications = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        assertNull(TransactionManager.currentOrNull())
                        generations++
                        super.nextBytes(bytes)
                    }
                }
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        assertNull(TransactionManager.currentOrNull())
                        assertNotNull(ApiCredentialAuthenticator(database).authenticate(id, token))
                        publications++
                    }
                }
            val creation = CreateApiKey(database, cache, bcryptCost = 10, random = random)
            for (type in ApiKeyType.entries) {
                val description = LocalizedText.of("Beskrivning", "Description")
                val result = creation.create(ApiKeyName("creation ${type.name}"), description, type)
                assertEquals(
                    result.apiKey,
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.findApiKeyIn(this, result.apiKey.id)
                    },
                )
                assertEquals(0, result.apiKey.version)
                assertTrue(result.token.value !in result.toString())
                if (type == ApiKeyType.INFO) {
                    assertEquals(
                        ApiKeyInfoSettings(
                            0,
                            emptyList(),
                        ),
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            keys.infoSettingsIn(this, result.apiKey.id)
                        },
                    )
                }
                if (type == ApiKeyType.ACCOUNT_SCAFFOLD) {
                    assertEquals(
                        ApiKeyAccountScaffoldSettings(0, emptyList()),
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            keys.accountScaffoldSettingsIn(this, result.apiKey.id)
                        },
                    )
                }
            }
            assertEquals(4, generations)
            assertEquals(4, publications)
        }

    @Test
    fun `failure inserting settings rolls back the key description and settings without cache publication`() =
        withDatabase { database ->
            var publications = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        publications++
                    }
                }
            val before = listOf("g_api_key", "g_api_key_settings", "g_text").map(database::tableRowCount)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_new_settings() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'new settings rejected'; END $$;
                CREATE TRIGGER reject_new_settings BEFORE INSERT ON g_api_key_settings
                FOR EACH ROW EXECUTE FUNCTION reject_new_settings();
                """.trimIndent(),
            )
            assertFailsWith<ExposedSQLException> {
                CreateApiKey(database, cache, bcryptCost = 10)
                    .create(ApiKeyName("failed creation"), LocalizedText.of(), ApiKeyType.INFO)
            }
            assertEquals(before, listOf("g_api_key", "g_api_key_settings", "g_text").map(database::tableRowCount))
            assertEquals(0, publications)
        }

    @Test
    fun `SQL retry reuses the prepared credential and publishes once`() {
        PostgresTestEnvironment().use { postgres ->
            var statements = 0
            var generations = 0
            var publications = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        statements++
                        // Fail after the description and key have been inserted on the first attempt.
                        if (statements == 3) throw SQLException("retry settings insertion")
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val random =
                    object : SecureRandom() {
                        override fun nextBytes(bytes: ByteArray) {
                            generations++
                            super.nextBytes(bytes)
                        }
                    }
                val cache =
                    object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                        override fun remember(
                            id: ApiKeyId,
                            storedCredential: StoredApiCredential,
                            token: RawApiToken,
                        ) {
                            publications++
                        }
                    }
                val before = database.tableRowCount("g_api_key")
                val result =
                    CreateApiKey(database, cache, bcryptCost = 10, random = random)
                        .create(ApiKeyName("retry creation"), LocalizedText.of(), ApiKeyType.INFO)
                assertEquals(6, statements)
                assertEquals(1, generations)
                assertEquals(1, publications)
                assertEquals(before + 1, database.tableRowCount("g_api_key"))
                assertNotNull(ApiCredentialAuthenticator(database).authenticate(result.apiKey.id, result.token))
            }
        }
    }

    @Test
    fun `explicit insertion rolls back with its caller and rejects foreign or completed transactions`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val creation = CreateApiKey(database, bcryptCost = 10)
                    val prepared =
                        creation.prepare(
                            ApiKeyName("participant creation"),
                            LocalizedText.of(),
                            ApiKeyType.INFO,
                        )
                    assertTrue(prepared.token.value !in prepared.toString())
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            creation.insertIn(this, prepared)
                            assertFailsWith<IllegalStateException> { creation.publishAfterCommit(prepared) }
                            throw IllegalArgumentException("later participant rejected creation")
                        }
                    }
                    assertNull(
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            ApiKeyQueries(database).findApiKeyIn(this, prepared.apiKey.id)
                        },
                    )
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> { creation.insertIn(this, prepared) }
                    }
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> { creation.insertIn(completed, prepared) }
                    val created = database.commitTransaction { creation.insertIn(this, prepared) }
                    assertNotNull(ApiCredentialAuthenticator(database).authenticate(created.apiKey.id, created.token))
                }
            }
        }
    }

    @Test
    fun `cache interruption propagates while the committed key remains usable`() =
        withDatabase { database ->
            val interruption = InterruptedException("cache interrupted")
            var committedId: ApiKeyId? = null
            var committedToken: RawApiToken? = null
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        committedId = id
                        committedToken = token
                        throw interruption
                    }
                }
            assertSame(
                interruption,
                assertFailsWith<InterruptedException> {
                    CreateApiKey(database, cache, bcryptCost = 10)
                        .create(ApiKeyName("interrupted creation"), LocalizedText.of(), ApiKeyType.INFO)
                },
            )
            assertNotNull(
                ApiCredentialAuthenticator(
                    database,
                ).authenticate(assertNotNull(committedId), assertNotNull(committedToken)),
            )
        }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }
}
