package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ApiCredentialAuthenticatorIntegrationTest {
    @Test
    fun `rotation during a cached match cannot authenticate the previous token`() =
        withDatabase { database ->
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch {
                        RotateApiKey(database, bcryptCost = 10).rotateForTest(database, id)
                        return CachedApiTokenMatch.MATCH
                    }
                }
            assertNull(ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken))
        }

    @Test
    fun `key type changed during verification cannot change the authenticated authority`() =
        withDatabase { database ->
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch {
                        database.executeSqlScript(
                            "UPDATE g_api_key SET key_type = 'ALLOW_LIST' WHERE api_key_id = '${id.value}'",
                        )
                        return CachedApiTokenMatch.MATCH
                    }
                }
            assertNull(ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken))
        }

    @Test
    fun `cache lookup cancellation propagates instead of performing fallback authentication`() =
        withDatabase { database ->
            val cancellation = CancellationException("lookup cancelled")
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch = throw cancellation
                }
            assertSame(
                cancellation,
                assertFailsWith<CancellationException> {
                    ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken)
                },
            )
        }

    @Test
    fun `cache population cancellation cannot look like successful authentication`() =
        withDatabase { database ->
            val cancellation = CancellationException("remember cancelled")
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
                    ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken)
                },
            )
        }

    @Test
    fun `an enclosing transaction is rejected before consulting the cache`() =
        withDatabase { database ->
            var lookups = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch {
                        lookups += 1
                        return CachedApiTokenMatch.MATCH
                    }
                }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken)
                }
            }
            assertEquals(0, lookups)
        }

    @Test
    fun `cache miss verifies the real token and returns current key details`() =
        withDatabase { database ->
            var matches = 0
            var remembers = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch {
                        assertNull(TransactionManager.currentOrNull())
                        assertTrue(database.ping())
                        matches += 1
                        return CachedApiTokenMatch.MISS
                    }

                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        assertNull(TransactionManager.currentOrNull())
                        assertEquals(fixtureId, id)
                        assertEquals(fixtureToken, token)
                        remembers += 1
                        database.executeSqlScript(
                            "UPDATE g_api_key SET pretty_name = 'Current name', version = 1 " +
                                "WHERE api_key_id = '${id.value}'",
                        )
                    }
                }
            val authentication = ApiCredentialAuthenticator(database, cache)
            val result =
                assertNotNull(authentication.authenticate(fixtureId, fixtureToken, requiredType = ApiKeyType.INFO))
            assertEquals(ApiKeyName("Current name"), result.name)
            assertEquals(1, result.version)
            assertNull(
                authentication.authenticate(fixtureId, RawApiToken("wrong-token-value-that-is-definitely-long-enough")),
            )
            assertEquals(2, matches)
            assertEquals(1, remembers)
        }

    @Test
    fun `missing keys wrong types and cache mismatches cannot authenticate`() =
        withDatabase { database ->
            var matches = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch {
                        matches += 1
                        return CachedApiTokenMatch.MISMATCH
                    }

                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) = error("Unexpected cache population")
                }
            val authentication = ApiCredentialAuthenticator(database, cache)
            assertNull(authentication.authenticate(ApiKeyId(UUID.randomUUID()), fixtureToken))
            assertNull(authentication.authenticate(fixtureId, fixtureToken, requiredType = ApiKeyType.CLIENT))
            assertEquals(0, matches)
            assertNull(authentication.authenticate(fixtureId, fixtureToken))
            assertEquals(1, matches)
        }

    @Test
    fun `rotation during cache population rejects the token just verified`() =
        withDatabase { database ->
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        RotateApiKey(database, bcryptCost = 10).rotateForTest(database, id)
                    }
                }
            assertNull(ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken))
        }

    @Test
    fun `deletion during cache lookup cannot authenticate a removed key`() =
        withDatabase { database ->
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch {
                        database.commitTransaction { DeleteApiKey(database).deleteIn(this, id) }
                        return CachedApiTokenMatch.MATCH
                    }
                }
            assertNull(ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken))
        }

    @Test
    fun `cache interruption propagates from lookup and population`() =
        withDatabase { database ->
            val lookup = InterruptedException("lookup interrupted")
            val lookupCache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun match(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        presentedToken: RawApiToken,
                    ): CachedApiTokenMatch = throw lookup
                }
            assertSame(
                lookup,
                assertFailsWith<InterruptedException> {
                    ApiCredentialAuthenticator(database, lookupCache).authenticate(fixtureId, fixtureToken)
                },
            )
            val population = InterruptedException("population interrupted")
            val populationCache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ): Unit = throw population
                }
            assertSame(
                population,
                assertFailsWith<InterruptedException> {
                    ApiCredentialAuthenticator(database, populationCache).authenticate(fixtureId, fixtureToken)
                },
            )
        }

    @Test
    fun `a final read retry does not repeat cache or hash work`() {
        PostgresTestEnvironment().use { postgres ->
            var armed = false
            var failures = 0
            var lookups = 0
            var populations = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (armed) {
                            armed = false
                            failures += 1
                            throw SQLException("retry final read")
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val cache =
                    object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                        override fun match(
                            id: ApiKeyId,
                            storedCredential: StoredApiCredential,
                            presentedToken: RawApiToken,
                        ): CachedApiTokenMatch {
                            lookups += 1
                            return CachedApiTokenMatch.MISS
                        }

                        override fun remember(
                            id: ApiKeyId,
                            storedCredential: StoredApiCredential,
                            token: RawApiToken,
                        ) {
                            populations += 1
                            armed = true
                        }
                    }
                assertNotNull(ApiCredentialAuthenticator(database, cache).authenticate(fixtureId, fixtureToken))
                assertEquals(1, failures)
                assertEquals(1, lookups)
                assertEquals(1, populations)
            }
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val fixtureId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
        val fixtureToken = RawApiToken("gamma-info-regression-token-000001")
    }
}
