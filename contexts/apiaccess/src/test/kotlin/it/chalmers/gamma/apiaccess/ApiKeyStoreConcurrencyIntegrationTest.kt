package it.chalmers.gamma.apiaccess

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.security.SecureRandom
import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiKeyStoreConcurrencyIntegrationTest {
    @Test
    fun `concurrent rotations keep versions and cached credentials consistent`() {
        postgresEnvironmentForConcurrency().use { postgres ->
            databaseForConcurrency(postgres).use { database ->
                val id = ApiKeyId.parse("33333333-3333-4333-8333-333333333333")
                val cache = ReverseCompletionVerificationCache()
                val api = ApiKeyStore(database, cache, bcryptCost = 10, random = SequencedSecureRandom())
                val blocker = postgres.dataSource.connection
                try {
                    blocker.lockApiKey(id)
                    val workers = Executors.newFixedThreadPool(2)
                    val replacements =
                        try {
                            val rotations = List(2) { workers.submit<RawApiToken> { api.resetToken(id) } }
                            waitForBlockedApiAccessTransactions(postgres, expected = 2)
                            blocker.commit()
                            rotations.map { it.get() }
                        } finally {
                            workers.shutdownNow()
                        }

                    assertNotEquals(replacements[0], replacements[1])
                    assertEquals(2, run { api.findApiKey(id) }?.version)
                    assertEquals(2, cache.entries.size)
                    cache.entries.forEach { (cacheKey, token) ->
                        val stored = cacheKey.second.value.removePrefix("{bcrypt}")
                        assertTrue(BCrypt.verifyer().verify(token.value.toCharArray(), stored.toCharArray()).verified)
                    }

                    val authoritativeApi = ApiKeyStore(database, bcryptCost = 10)
                    val winningTokens =
                        replacements.filter { token ->
                            run { authoritativeApi.authenticate(ApiKeyType.ALLOW_LIST, id, token) } != null
                        }
                    assertEquals(1, winningTokens.size)
                    val winner = winningTokens.single()
                    val loser = replacements.single { it != winner }
                    assertNotNull(run { api.authenticate(ApiKeyType.ALLOW_LIST, id, winner) })
                    assertNull(run { api.authenticate(ApiKeyType.ALLOW_LIST, id, loser) })
                } finally {
                    blocker.rollback()
                    blocker.close()
                }
            }
        }
    }

    @Test
    fun `concurrent deletes report one success and one missing key`() {
        postgresEnvironmentForConcurrency().use { postgres ->
            databaseForConcurrency(postgres).use { database ->
                val id = ApiKeyId.parse("33333333-3333-4333-8333-333333333333")
                val api = ApiKeyStore(database, bcryptCost = 10)
                val blocker = postgres.dataSource.connection
                try {
                    blocker.lockApiKey(id)
                    val workers = Executors.newFixedThreadPool(2)
                    val results =
                        try {
                            val deletions =
                                List(2) {
                                    workers.submit<ApiAccessNotFound?> {
                                        try {
                                            api.deleteApiKey(id)
                                            null
                                        } catch (failure: ApiAccessNotFound) {
                                            failure
                                        }
                                    }
                                }
                            waitForBlockedApiAccessTransactions(postgres, expected = 2)
                            blocker.commit()
                            deletions.map { it.get() }
                        } finally {
                            workers.shutdownNow()
                        }

                    assertEquals(1, results.count { it == null })
                    val failure = assertIs<ApiAccessNotFound>(results.single { it != null })
                    assertEquals("API key does not exist", failure.message)
                    assertNull(run { api.findApiKey(id) })
                } finally {
                    blocker.rollback()
                    blocker.close()
                }
            }
        }
    }
}

private class ReverseCompletionVerificationCache : ApiTokenVerificationCache {
    val entries = ConcurrentHashMap<Pair<ApiKeyId, StoredApiCredential>, RawApiToken>()
    private val arrivals = AtomicInteger()
    private val secondArrival = CountDownLatch(1)

    override fun match(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        presentedToken: RawApiToken,
    ): CachedApiTokenMatch {
        val expected = entries[id to storedCredential] ?: return CachedApiTokenMatch.MISS
        return if (expected == presentedToken) CachedApiTokenMatch.MATCH else CachedApiTokenMatch.MISMATCH
    }

    override fun remember(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    ) {
        if (arrivals.incrementAndGet() == 1) {
            secondArrival.await()
        } else {
            secondArrival.countDown()
        }
        entries[id to storedCredential] = token
    }
}

private class SequencedSecureRandom : SecureRandom() {
    private val sequence = AtomicInteger()

    override fun nextBytes(bytes: ByteArray) {
        bytes.fill(sequence.incrementAndGet().toByte())
    }
}

private fun Connection.lockApiKey(id: ApiKeyId) {
    prepareStatement("SELECT api_key_id FROM g_api_key WHERE api_key_id = ? FOR UPDATE").use { statement ->
        statement.setObject(1, id.value)
        statement.executeQuery().use { result -> check(result.next()) }
    }
}

private fun waitForBlockedApiAccessTransactions(
    postgres: PostgresTestEnvironment,
    expected: Int,
) {
    repeat(200) {
        val blocked =
            postgres.connection { connection ->
                connection
                    .prepareStatement(
                        "SELECT COUNT(*) FROM pg_stat_activity " +
                            "WHERE datname = current_database() " +
                            "AND cardinality(pg_blocking_pids(pid)) > 0 " +
                            "AND query LIKE '%g_api_key%'",
                    ).use { statement ->
                        statement.executeQuery().use { result ->
                            check(result.next())
                            result.getInt(1)
                        }
                    }
            }
        if (blocked >= expected) return
        Thread.sleep(25)
    }
    error("Timed out waiting for $expected blocked API-access transactions")
}

private fun postgresEnvironmentForConcurrency(): PostgresTestEnvironment {
    val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
    return PostgresTestEnvironment(listOf("filesystem:${root.resolve("app/src/main/resources/db/migration")}"))
}

private fun databaseForConcurrency(postgres: PostgresTestEnvironment) =
    DatabaseFactory(
        DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
    )
