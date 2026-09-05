package it.chalmers.gamma.apiaccess

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
import java.util.UUID
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RotateApiKeyIntegrationTest {
    @Test
    fun `rotation rejects an enclosing transaction before preparing credentials`() =
        withDatabase { database ->
            var preparations = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        preparations++
                        super.nextBytes(bytes)
                    }
                }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    RotateApiKey(
                        database,
                        bcryptCost = 10,
                        random = random,
                    ).prepare(fixtureId)
                }
            }
            assertEquals(0, preparations)
            assertEquals(
                0,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                    }?.version,
            )
        }

    @Test
    fun `rotation cache cancellation propagates after commit`() =
        withDatabase { database ->
            val cancellation = CancellationException("rotation cache cancelled")
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ): Unit = throw cancellation
                }
            assertSame(
                cancellation,
                assertFailsWith<CancellationException> {
                    RotateApiKey(database, cache, bcryptCost = 10).rotateForTest(database, fixtureId)
                },
            )
            assertEquals(
                1,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                    }?.version,
            )
        }

    @Test
    fun `rotation commits a usable token and returns current metadata with nullable version support`() =
        withDatabase { database ->
            database.executeSqlScript("UPDATE g_api_key SET version = NULL WHERE api_key_id = '${fixtureId.value}'")
            var publications = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        assertNull(TransactionManager.currentOrNull())
                        assertEquals(1, ApiCredentialAuthenticator(database).authenticate(id, token)?.version)
                        publications++
                    }
                }
            val result = RotateApiKey(database, cache, bcryptCost = 10).rotateForTest(database, fixtureId)
            assertEquals(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                },
                result.apiKey,
            )
            assertEquals(1, publications)
            assertTrue(result.token.value !in result.toString())
            assertNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, oldToken))
        }

    @Test
    fun `missing keys and required type mismatch reject before randomness`() =
        withDatabase { database ->
            var preparations = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        preparations++
                        super.nextBytes(bytes)
                    }
                }
            val rotation = RotateApiKey(database, bcryptCost = 10, random = random)
            assertFailsWith<ApiAccessNotFound> { rotation.prepare(ApiKeyId(UUID.randomUUID())) }
            assertFailsWith<ApiAccessConflict> { rotation.prepare(fixtureId, requiredType = ApiKeyType.CLIENT) }
            assertEquals(0, preparations)
        }

    @Test
    fun `deletion during preparation cannot publish a replacement credential`() =
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
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        assertNull(TransactionManager.currentOrNull())
                        database.commitTransaction { DeleteApiKey(database).deleteIn(this, fixtureId) }
                        super.nextBytes(bytes)
                    }
                }
            assertFailsWith<ApiAccessNotFound> {
                RotateApiKey(database, cache, bcryptCost = 10, random = random).rotateForTest(database, fixtureId)
            }
            assertEquals(0, publications)
            assertNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                },
            )
        }

    @Test
    fun `a type change during preparation rejects without rotating the credential`() =
        withDatabase { database ->
            val rotation = RotateApiKey(database, bcryptCost = 10)
            val prepared = rotation.prepare(fixtureId)
            database.executeSqlScript(
                "UPDATE g_api_key SET key_type = 'CLIENT' WHERE api_key_id = '${fixtureId.value}'",
            )
            assertFailsWith<ApiAccessConflict> { database.commitTransaction { rotation.replaceIn(this, prepared) } }
            assertEquals(
                0,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                    }?.version,
            )
            assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, oldToken))
        }

    @Test
    fun `failed SQL rolls back the credential and does not publish it`() =
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
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_key_rotation() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'rotation rejected'; END $$;
                CREATE TRIGGER reject_key_rotation BEFORE UPDATE ON g_api_key
                FOR EACH ROW EXECUTE FUNCTION reject_key_rotation();
                """.trimIndent(),
            )
            assertFailsWith<ExposedSQLException> {
                RotateApiKey(database, cache, bcryptCost = 10).rotateForTest(database, fixtureId)
            }
            assertEquals(0, publications)
            assertEquals(
                0,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                    }?.version,
            )
            assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, oldToken))
        }

    @Test
    fun `a SQL retry reuses one prepared credential`() {
        PostgresTestEnvironment().use { postgres ->
            var armed = false
            var retries = 0
            var preparations = 0
            var publications = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (armed) {
                            armed = false
                            retries++
                            throw SQLException("retry rotation")
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val random =
                    object : SecureRandom() {
                        override fun nextBytes(bytes: ByteArray) {
                            preparations++
                            armed = true
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
                val result =
                    RotateApiKey(
                        database,
                        cache,
                        bcryptCost = 10,
                        random = random,
                    ).rotateForTest(database, fixtureId)
                assertEquals(1, result.apiKey.version)
                assertEquals(1, preparations)
                assertEquals(1, retries)
                assertEquals(1, publications)
                assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, result.token))
            }
        }
    }

    @Test
    fun `lost commit acknowledgement does not increment the rotation version twice`() {
        PostgresTestEnvironment().use { postgres ->
            var armed = false
            var retries = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun afterCommit(transaction: Transaction) {
                        if (armed) {
                            armed = false
                            retries++
                            throw SQLException("lost rotation acknowledgement")
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val random =
                    object : SecureRandom() {
                        override fun nextBytes(bytes: ByteArray) {
                            armed = true
                            super.nextBytes(bytes)
                        }
                    }
                val result = RotateApiKey(database, bcryptCost = 10, random = random).rotateForTest(database, fixtureId)
                assertEquals(1, retries)
                assertEquals(1, result.apiKey.version)
                assertEquals(
                    1,
                    database
                        .commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                        }?.version,
                )
                assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, result.token))
            }
        }
    }

    @Test
    fun `a retry after lost acknowledgement cannot overwrite a newer committed rotation`() {
        PostgresTestEnvironment().use { postgres ->
            var armed = false
            var winner: RotatedApiKey? = null
            var publications = 0
            lateinit var database: DatabaseFactory
            val interceptor =
                object : StatementInterceptor {
                    override fun afterCommit(transaction: Transaction) {
                        if (!armed) return
                        armed = false
                        val executor = Executors.newSingleThreadExecutor()
                        try {
                            winner =
                                executor
                                    .submit<RotatedApiKey> {
                                        RotateApiKey(database, bcryptCost = 10).rotateForTest(database, fixtureId)
                                    }.get(20, TimeUnit.SECONDS)
                        } finally {
                            executor.shutdownNow()
                        }
                        throw SQLException("lost acknowledgement after a newer rotation")
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { factory ->
                database = factory
                val random =
                    object : SecureRandom() {
                        override fun nextBytes(bytes: ByteArray) {
                            armed = true
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
                assertFailsWith<ApiAccessConflict> {
                    RotateApiKey(database, cache, bcryptCost = 10, random = random).rotateForTest(database, fixtureId)
                }
                assertEquals(0, publications)
                assertEquals(
                    2,
                    database
                        .commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                        }?.version,
                )
                assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, assertNotNull(winner).token))
            }
        }
    }

    @Test
    fun `rotation participation shares rollback and rejects invalid transaction handles`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val rotation = RotateApiKey(database, bcryptCost = 10)
                    val prepared = rotation.prepare(fixtureId)
                    assertTrue(prepared.token.value !in prepared.toString())
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            rotation.replaceIn(this, prepared)
                            assertFailsWith<IllegalStateException> { rotation.publishAfterCommit(prepared) }
                            throw IllegalArgumentException("caller rejected rotation")
                        }
                    }
                    assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, oldToken))
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> {
                            rotation.replaceIn(
                                this,
                                prepared,
                            )
                        }
                    }
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> { rotation.replaceIn(completed, prepared) }
                }
            }
        }
    }

    @Test
    fun `cache interruption propagates after the replacement commits`() =
        withDatabase { database ->
            val interruption = InterruptedException("rotation interrupted")
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ): Unit = throw interruption
                }
            assertSame(
                interruption,
                assertFailsWith<InterruptedException> {
                    RotateApiKey(database, cache, bcryptCost = 10).rotateForTest(database, fixtureId)
                },
            )
            assertEquals(
                1,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                    }?.version,
            )
        }

    @Test
    fun `a skipped update cannot report a successful rotation`() =
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
            database.executeSqlScript(
                """
                CREATE FUNCTION skip_key_rotation() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RETURN NULL; END $$;
                CREATE TRIGGER skip_key_rotation BEFORE UPDATE ON g_api_key
                FOR EACH ROW EXECUTE FUNCTION skip_key_rotation();
                """.trimIndent(),
            )
            assertFailsWith<ApiAccessNotFound> {
                RotateApiKey(
                    database,
                    cache,
                    bcryptCost = 10,
                ).rotateForTest(database, fixtureId)
            }
            assertEquals(0, publications)
            assertEquals(
                0,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                    }?.version,
            )
            assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, oldToken))
        }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val oldToken = RawApiToken("gamma-info-regression-token-000001")
        val fixtureId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
    }
}
