package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserConcurrencyIntegrationTest {
    @Test
    fun `create rejects an existing email regardless of case`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val persistence = RegisterUser(database, AlwaysMatchingPasswordHasher)

            run {
                val existing = assertNotNull(queries.findUser(Cid("mscott")))
                val failure =
                    assertFailsWith<UserConflict> {
                        persistence.createActivatedTestUser(
                            database,
                            newUser(Cid("emailcase"), Email(existing.email.value.uppercase())),
                        )
                    }

                assertEquals("Email is already in use", failure.message)
            }
        }

    @Test
    fun `update rejects another users email regardless of case`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val emails = UpdateMyEmail(database)

            run {
                val first = assertNotNull(queries.findUser(Cid("mscott")))
                val second = assertNotNull(queries.findUser(Cid("jhalpert")))
                emails.update(first.profileActor(), Email("shared@example.org"))

                val failure =
                    assertFailsWith<UserConflict> {
                        emails.update(second.profileActor(), Email("SHARED@EXAMPLE.ORG"))
                    }

                assertEquals("Email is already in use", failure.message)
                assertEquals(first.id, queries.findUser(Email("Shared@Example.org"))?.id)
            }
        }

    @Test
    fun `concurrent creates expose one case insensitive email conflict and one winner`() =
        withTwoUserDatabases(maximumPoolSize = 2) { firstDatabase, secondDatabase ->
            firstDatabase.installEmailInsertGate()
            val firstLifecycle = RegisterUser(firstDatabase, AlwaysMatchingPasswordHasher)
            val secondLifecycle = RegisterUser(secondDatabase, AlwaysMatchingPasswordHasher)
            val queries = UserQueries(firstDatabase)

            Executors.newFixedThreadPool(2).use { workers ->
                val blocker = holdEmailInsertGate(firstDatabase)
                val attempts =
                    listOf(
                        workers.submit<UserCreationOutcome> {
                            createUserOutcome(
                                firstLifecycle,
                                firstDatabase,
                                newUser(Cid("raceone"), Email("Race.Email@example.org")),
                            )
                        },
                        workers.submit<UserCreationOutcome> {
                            createUserOutcome(
                                secondLifecycle,
                                secondDatabase,
                                newUser(Cid("racetwo"), Email("race.email@EXAMPLE.ORG")),
                            )
                        },
                    )

                try {
                    secondDatabase.awaitEmailInsertGateWaiters(expected = 2)
                } finally {
                    blocker.release.countDown()
                    blocker.thread.join()
                }
                val results = attempts.map { it.get() }

                assertEquals(1, results.count { it.userId != null })
                val failure = assertNotNull(results.single { it.failure != null }.failure)
                assertEquals("Email is already in use", failure.message)
                val stored = assertNotNull(queries.findUser(Email("RACE.EMAIL@example.org")))
                assertEquals("race.email@example.org", stored.email.value)
            }
        }

    @Test
    fun `password verification releases its database connection before hashing work`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val hasher = BlockingPasswordHasher()
            val authentication = UserAuthentication(database, hasher)
            val userId = run { assertNotNull(UserQueries(database).findUser(Cid("mscott"))).id }

            Executors.newSingleThreadExecutor().use { workers ->
                val verification =
                    workers.submit<Boolean> {
                        authentication.authenticate(userId, PlainTextPassword("password1337")) !=
                            null
                    }
                assertTrue(hasher.awaitWork())
                try {
                    assertTrue(database.ping())
                } finally {
                    hasher.releaseWork()
                }
                assertTrue(verification.get())
            }
        }

    private fun newUser(
        cid: Cid,
        email: Email,
    ) = NewUser(
        cid = cid,
        nick = Nick("Email uniqueness"),
        firstName = FirstName("Test"),
        lastName = LastName("User"),
        acceptanceYear = AcceptanceYear.of(2020, currentYear = 2026),
        language = Language.EN,
        email = email,
        password = PlainTextPassword("correct horse battery staple"),
    )
}

private fun createUserOutcome(
    lifecycle: RegisterUser,
    database: DatabaseFactory,
    user: NewUser,
): UserCreationOutcome =
    try {
        UserCreationOutcome(
            lifecycle.createActivatedTestUser(database, user),
            failure = null,
        )
    } catch (failure: UserConflict) {
        UserCreationOutcome(userId = null, failure = failure)
    }

private fun DatabaseFactory.installEmailInsertGate() {
    executeSqlScript(
        """
        CREATE FUNCTION await_racing_email_insert() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
        BEGIN
            IF NEW.email = 'race.email@example.org' THEN
                PERFORM pg_advisory_xact_lock($EMAIL_INSERT_GATE_NAMESPACE, $EMAIL_INSERT_GATE_ID);
            END IF;
            RETURN NEW;
        END;
        ${'$'}${'$'};
        CREATE TRIGGER await_racing_email_insert
            BEFORE INSERT ON g_user
            FOR EACH ROW EXECUTE FUNCTION await_racing_email_insert();
        """.trimIndent(),
    )
}

private fun holdEmailInsertGate(database: DatabaseFactory): HeldEmailInsertGate {
    val acquired = CountDownLatch(1)
    val release = CountDownLatch(1)
    val thread =
        Thread.startVirtualThread {
            database.commitTransaction {
                exec("SELECT pg_advisory_xact_lock($EMAIL_INSERT_GATE_NAMESPACE, $EMAIL_INSERT_GATE_ID)")
                acquired.countDown()
                release.await()
            }
        }
    acquired.await()
    return HeldEmailInsertGate(release, thread)
}

private fun DatabaseFactory.awaitEmailInsertGateWaiters(expected: Int) {
    repeat(500) {
        if (emailInsertGateWaiterCount() == expected) return
        Thread.sleep(10)
    }
    error("Timed out waiting for $expected email insert gate waiters")
}

private fun DatabaseFactory.emailInsertGateWaiterCount(): Int =
    commitTransaction(readOnly = true) {
        exec(
            """
            SELECT COUNT(*)
            FROM pg_locks
            WHERE locktype = 'advisory'
              AND database = (SELECT oid FROM pg_database WHERE datname = current_database())
              AND classid = $EMAIL_INSERT_GATE_NAMESPACE
              AND objid = $EMAIL_INSERT_GATE_ID
              AND objsubid = 2
              AND mode = 'ExclusiveLock'
              AND NOT granted
            """.trimIndent(),
        ) { result ->
            check(result.next())
            result.getInt(1)
        } ?: 0
    }

private data class UserCreationOutcome(
    val userId: UserId?,
    val failure: UserConflict?,
)

private data class HeldEmailInsertGate(
    val release: CountDownLatch,
    val thread: Thread,
)

private class BlockingPasswordHasher : PasswordHasher {
    private val workStarted = CountDownLatch(1)
    private val workRelease = CountDownLatch(1)

    override fun hash(password: PlainTextPassword): PasswordHash {
        blockWork()
        return PasswordHash("{bcrypt}\$replacement")
    }

    override fun verify(
        password: PlainTextPassword,
        hash: PasswordHash,
    ): Boolean {
        blockWork()
        return true
    }

    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean {
        blockWork()
        return false
    }

    fun awaitWork(): Boolean = workStarted.await(5, TimeUnit.SECONDS)

    fun releaseWork() = workRelease.countDown()

    private fun blockWork() {
        workStarted.countDown()
        check(workRelease.await(5, TimeUnit.SECONDS)) { "Timed out waiting to release password work" }
    }
}

private const val EMAIL_INSERT_GATE_NAMESPACE = 7827
private const val EMAIL_INSERT_GATE_ID = 1471
