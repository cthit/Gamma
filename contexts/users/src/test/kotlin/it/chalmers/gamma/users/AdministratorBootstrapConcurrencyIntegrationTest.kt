package it.chalmers.gamma.users

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
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
            val firstBootstrap = UserBootstrap(firstDatabase, passwordHasher)
            val secondBootstrap = UserBootstrap(secondDatabase, passwordHasher)

            Executors.newFixedThreadPool(2).use { workers ->
                val start = CountDownLatch(1)
                val attempts =
                    listOf(firstBootstrap, secondBootstrap).map { bootstrap ->
                        workers.submit<AdministratorBootstrapResult> {
                            start.await()
                            bootstrap.ensureAdministrator(PlainTextPassword("bootstrap password"))
                        }
                    }
                start.countDown()

                assertEquals(
                    listOf(AdministratorBootstrapResult.ALREADY_CONFIGURED, AdministratorBootstrapResult.CREATED),
                    attempts.map { it.get() }.sortedBy(AdministratorBootstrapResult::name),
                )
                val queries = UserQueries(firstDatabase)
                val administrator = checkNotNull(queries.findUser(Cid("admin")))
                firstDatabase.commitTransaction(readOnly = true) {
                    assertTrue(
                        AdminUsersTable
                            .selectAll()
                            .where { AdminUsersTable.userId eq administrator.id.value }
                            .any(),
                    )
                    assertTrue(
                        GdprTrainedUsersTable
                            .selectAll()
                            .where { GdprTrainedUsersTable.userId eq administrator.id.value }
                            .any(),
                    )
                }
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
