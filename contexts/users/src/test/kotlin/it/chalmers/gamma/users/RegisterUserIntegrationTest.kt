package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.sql.SQLException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegisterUserIntegrationTest {
    @Test
    fun `registration binds CID to the token hashes without a connection and consumes a digest token once`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val token = database.seedActivationForTest(cid)
            database.executeSqlScript(
                "UPDATE g_user_activation SET token = '${storedToken(token.value)}' WHERE cid = 'student'",
            )
            var hashes = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        assertTrue(database.ping())
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            val operation = RegisterUser(database, hasher)
            val input = registration(token)
            val userId = operation.register(Actor.Anonymous, input)
            val user = assertNotNull(UserQueries(database).findUser(userId))
            assertEquals(cid, user.cid)
            assertEquals(input.nick, user.nick)
            assertEquals(Email("student@example.org"), user.email)
            assertEquals(input.acceptanceYear, user.acceptanceYear)
            assertEquals(input.language, user.language)
            assertEquals(0, user.version)
            assertEquals(false, user.locked)
            assertNull(activations.findCid(token))
            assertEquals(false, activations.allowedCids().contains(cid))
            assertFailsWith<UserConflict> { operation.register(Actor.Anonymous, input) }
            assertEquals(1, hashes)
            assertEquals("UserRegistration(<redacted>)", input.toString())
        }

    @Test
    fun `actor agreement password language and token failures precede hashing`() =
        withUserDatabase { database ->
            val operation = RegisterUser(database, UnexpectedRegistrationHasher)
            val input = registration(RegistrationToken("x".repeat(72)))
            val actor = Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value), true)
            assertFailsWith<AccessDenied> { operation.register(actor, input) }
            assertFailsWith<IllegalArgumentException> {
                operation.register(Actor.Anonymous, input.copy(language = null))
            }
            assertFailsWith<UserConflict> {
                operation.register(Actor.Anonymous, input.copy(confirmedPassword = "different"))
            }
            assertFailsWith<UserConflict> {
                operation.register(Actor.Anonymous, input.copy(acceptedUserAgreement = false))
            }
            assertFailsWith<UserConflict> { operation.register(Actor.Anonymous, input) }
            assertNull(UserQueries(database).findUser(cid))
        }

    @Test
    fun `replacement expiry and retraction during hashing invalidate the earlier token read`() =
        withUserDatabase { database ->
            val activations = ActivationCodes(database)
            for (change in listOf("replacement", "expiry", "retraction")) {
                if (!activations.allowedCids().contains(cid)) activations.allow(cid)
                val token = database.seedActivationForTest(cid)
                var replacement: RegistrationToken? = null
                val hasher =
                    object : PasswordHasher by AlwaysMatchingPasswordHasher {
                        override fun hash(password: PlainTextPassword): PasswordHash {
                            when (change) {
                                "replacement" -> {
                                    replacement = database.seedActivationForTest(cid)
                                }

                                "expiry" -> {
                                    database.executeSqlScript(
                                        "UPDATE g_user_activation SET created_at = " +
                                            "NOW() AT TIME ZONE 'UTC' - INTERVAL '16 minutes'",
                                    )
                                }

                                "retraction" -> {
                                    ActivationCodeAdministration(database).retractCid(
                                        Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value)),
                                        cid,
                                    )
                                }
                            }
                            return AlwaysMatchingPasswordHasher.hash(password)
                        }
                    }
                assertFailsWith<UserConflict> {
                    RegisterUser(database, hasher).register(Actor.Anonymous, registration(token))
                }
                assertNull(UserQueries(database).findUser(cid))
                assertEquals(change != "retraction", activations.allowedCids().contains(cid))
                replacement?.let { assertEquals(cid, activations.findCid(it)) }
            }
        }

    @Test
    fun `insertion failure restores token and allow-list entry without repeating password work`() =
        withUserDatabase { database ->
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val token = database.seedActivationForTest(cid)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_registration() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected registration failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_registration BEFORE INSERT ON g_user
                FOR EACH ROW EXECUTE FUNCTION reject_registration();
                """.trimIndent(),
            )
            var hashes = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            assertFailsWith<SQLException> {
                RegisterUser(database, hasher).register(Actor.Anonymous, registration(token))
            }
            assertEquals(1, hashes)
            assertEquals(cid, activations.findCid(token))
            assertTrue(activations.allowedCids().contains(cid))
            assertNull(UserQueries(database).findUser(cid))
        }

    @Test
    fun `duplicate email preserves a usable activation`() =
        withUserDatabase { database ->
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val token = database.seedActivationForTest(cid)
            val existing = assertNotNull(UserQueries(database).findUser(Cid("mscott")))
            val input = registration(token).copy(email = Email(existing.email.value.uppercase()))
            val failure =
                assertFailsWith<UserConflict> {
                    RegisterUser(database, AlwaysMatchingPasswordHasher).register(Actor.Anonymous, input)
                }
            assertEquals("Email is already in use", failure.message)
            assertEquals(cid, activations.findCid(token))
            assertTrue(activations.allowedCids().contains(cid))
            assertNull(UserQueries(database).findUser(cid))
        }

    @Test
    fun `competing registrations consume one token and create one identity`() =
        withUserDatabase { database ->
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val input = registration(database.seedActivationForTest(cid))
            val hashing = CountDownLatch(2)
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashing.countDown()
                        check(hashing.await(10, TimeUnit.SECONDS))
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            val operation = RegisterUser(database, hasher)
            Executors.newFixedThreadPool(2).use { workers ->
                val attempts =
                    List(2) {
                        workers.submit<UserId?> {
                            try {
                                operation.register(Actor.Anonymous, input)
                            } catch (_: UserConflict) {
                                null
                            }
                        }
                    }
                val winners = attempts.mapNotNull { it.get(15, TimeUnit.SECONDS) }
                assertEquals(1, winners.size)
                assertEquals(winners.single(), UserQueries(database).findUser(cid)?.id)
                assertNull(activations.findCid(input.token))
            }
        }

    @Test
    fun `registration waits for the allow-list row before locking the activation`() =
        withUserDatabase(maximumPoolSize = 2) { database ->
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val input = registration(database.seedActivationForTest(cid))
            Executors.newSingleThreadExecutor().use { worker ->
                val attempt =
                    database.commitTransaction {
                        maxAttempts = 1
                        exec("SELECT cid FROM g_allow_list WHERE cid = 'student' FOR UPDATE") { it.next() }
                        val registration =
                            worker.submit<UserId> {
                                RegisterUser(database, AlwaysMatchingPasswordHasher).register(Actor.Anonymous, input)
                            }
                        awaitRegistrationLock("g_allow_list")
                        exec("SET LOCAL lock_timeout = '500ms'")
                        // Issuance/retraction can acquire this row while holding the allow-list lock.
                        exec("SELECT cid FROM g_user_activation WHERE cid = 'student' FOR UPDATE") {
                            assertTrue(it.next())
                        }
                        registration
                    }
                assertEquals(attempt.get(10, TimeUnit.SECONDS), UserQueries(database).findUser(cid)?.id)
            }
        }

    @Test
    fun `token expiry is evaluated after waiting for its row lock`() =
        withUserDatabase(maximumPoolSize = 2) { database ->
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val input = registration(database.seedActivationForTest(cid))
            Executors.newSingleThreadExecutor().use { worker ->
                val attempt =
                    database.commitTransaction {
                        maxAttempts = 1
                        exec(
                            "UPDATE g_user_activation SET created_at = clock_timestamp() AT TIME ZONE 'UTC' " +
                                "- INTERVAL '14 minutes 58 seconds' WHERE cid = 'student'",
                        )
                        val registration =
                            worker.submit<UserId> {
                                RegisterUser(database, AlwaysMatchingPasswordHasher).register(Actor.Anonymous, input)
                            }
                        awaitRegistrationLock("g_user_activation")
                        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
                        while (true) {
                            val expired =
                                exec(
                                    "SELECT created_at + INTERVAL '15 minutes' <= " +
                                        "clock_timestamp() AT TIME ZONE 'UTC' " +
                                        "FROM g_user_activation WHERE cid = 'student'",
                                ) { rows ->
                                    rows.next()
                                    rows.getBoolean(1)
                                } == true
                            if (expired) break
                            check(System.nanoTime() < deadline) { "Token did not expire" }
                            Thread.sleep(10)
                        }
                        registration
                    }
                val failure = assertFailsWith<ExecutionException> { attempt.get(10, TimeUnit.SECONDS) }
                assertIs<UserConflict>(failure.cause)
                assertNull(UserQueries(database).findUser(cid))
                assertTrue(activations.allowedCids().contains(cid))
            }
        }

    private val cid = Cid("student")

    private fun registration(token: RegistrationToken) =
        UserRegistration(
            token,
            Nick("Student"),
            FirstName("Student"),
            LastName("User"),
            AcceptanceYear.of(2021, 2026),
            Language.EN,
            Email("STUDENT@EXAMPLE.ORG"),
            PlainTextPassword("password1337"),
            "password1337",
            true,
        )
}

private fun JdbcTransaction.awaitRegistrationLock(table: String) {
    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10)
    while (true) {
        exec("SELECT pg_stat_clear_snapshot()")
        val waiting =
            exec(
                "SELECT COUNT(*) FROM pg_stat_activity WHERE datname = current_database() " +
                    "AND pid <> pg_backend_pid() AND wait_event_type = 'Lock' AND query LIKE '%$table%'",
            ) { rows ->
                rows.next()
                rows.getInt(1)
            } == 1
        if (waiting) return
        check(System.nanoTime() < deadline) { "Registration did not reach its database lock" }
        Thread.sleep(10)
    }
}

private object UnexpectedRegistrationHasher : PasswordHasher by AlwaysMatchingPasswordHasher {
    override fun hash(password: PlainTextPassword): PasswordHash = error("Unexpected hashing")
}
