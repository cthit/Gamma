package it.chalmers.gamma.users

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdministratorBootstrapConcurrencyIntegrationTest {
    @Test
    fun `bootstrap attempts that reach password hashing together create one complete administrator`() =
        withTwoUserDatabases(loadRegressionFixture = false) { firstDatabase, secondDatabase ->
            val passwordHasher = ConcurrentBootstrapPasswordHasher()
            val firstStore = UserStore(firstDatabase, passwordHasher)
            val secondStore = UserStore(secondDatabase, passwordHasher)

            Executors.newFixedThreadPool(2).use { workers ->
                val start = CountDownLatch(1)
                val attempts =
                    listOf(firstStore, secondStore).map { store ->
                        workers.submit<AdministratorBootstrapResult> {
                            start.await()
                            UserBootstrap(store).ensureAdministrator(PlainTextPassword("bootstrap password"))
                        }
                    }
                start.countDown()

                assertEquals(
                    listOf(AdministratorBootstrapResult.ALREADY_CONFIGURED, AdministratorBootstrapResult.CREATED),
                    attempts.map { it.get() }.sortedBy(AdministratorBootstrapResult::name),
                )
                val queries = UserStoreForQueries(firstDatabase)
                val administrator = checkNotNull(queries.findUser(Cid("admin")))
                assertTrue(queries.isAdministrator(administrator.id))
                assertTrue(queries.isGdprTrained(administrator.id))
            }
        }
}

private class ConcurrentBootstrapPasswordHasher : PasswordHasher {
    private val hashingAttempts = CountDownLatch(2)

    override fun hash(password: PlainTextPassword): PasswordHash {
        hashingAttempts.countDown()
        check(hashingAttempts.await(5, TimeUnit.SECONDS)) { "Bootstrap attempts did not overlap during hashing" }
        return PasswordHash("{bcrypt}\$concurrent-bootstrap")
    }

    override fun verify(
        password: PlainTextPassword,
        hash: PasswordHash,
    ): Boolean = true

    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean = false
}
