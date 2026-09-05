package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.sql.SQLException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResetPasswordIntegrationTest {
    @Test
    fun `raw and digest tokens bind the user consume once and hash without a connection`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            database.executeSqlScript("UPDATE g_user SET version = NULL WHERE cid = 'jhalpert'")
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val other = assertNotNull(UserQueries(database).findUser(Cid("mscott")))
            val otherCredential = database.findPasswordUser(other.id)
            val resets = PasswordResets(database)
            val bcrypt = BcryptPasswordHasher(cost = 10)
            var hashes = 0
            val hasher =
                object : PasswordHasher by bcrypt {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        assertTrue(database.ping())
                        return bcrypt.hash(password)
                    }
                }
            val operation = ResetPassword(database, hasher)
            for (digest in listOf(false, true)) {
                val token = database.seedPasswordResetForTest(user.id)
                if (digest) {
                    database.executeSqlScript(
                        "UPDATE g_password_reset SET token = '${storedToken(token.value)}' " +
                            "WHERE user_id = '${user.id.value}'",
                    )
                }
                val input = completion(token)
                operation.reset(Actor.Anonymous, input)
                val saved = assertNotNull(database.findPasswordUser(user.id))
                assertEquals(hashes, saved.version)
                assertTrue(bcrypt.verify(input.password, assertNotNull(saved.passwordHash)))
                assertFalse(bcrypt.verify(PlainTextPassword("password1337"), saved.passwordHash))
                assertEquals(user.copy(version = hashes), UserQueries(database).findUser(user.id))
                assertEquals(otherCredential, database.findPasswordUser(other.id))
                assertNull(resets.findUser(token))
                assertFailsWith<UserConflict> { operation.reset(Actor.Anonymous, input) }
                assertEquals("PasswordResetCompletion(<redacted>)", input.toString())
            }
            assertEquals(2, hashes)
        }

    @Test
    fun `actor confirmation unknown expired and ambient requests fail before hashing`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val before = database.findPasswordUser(user.id)
            val token = database.seedPasswordResetForTest(user.id)
            val operation = ResetPassword(database, UnexpectedResetHasher)
            assertFailsWith<AccessDenied> { operation.reset(user.profileActor(), completion(token)) }
            assertFailsWith<UserConflict> {
                operation.reset(Actor.Anonymous, completion(token).copy(confirmedPassword = "different"))
            }
            assertFailsWith<UserConflict> {
                operation.reset(Actor.Anonymous, completion(PasswordResetToken("x".repeat(72))))
            }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { operation.reset(Actor.Anonymous, completion(token)) }
            }
            database.executeSqlScript(
                "UPDATE g_password_reset SET created_at = " +
                    "clock_timestamp() AT TIME ZONE 'UTC' - INTERVAL '16 minutes'",
            )
            assertFailsWith<UserConflict> { operation.reset(Actor.Anonymous, completion(token)) }
            assertEquals(before, database.findPasswordUser(user.id))
        }

    @Test
    fun `replacement expiry and deletion during hashing invalidate the earlier token read`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val before = database.findPasswordUser(user.id)
            val resets = PasswordResets(database)
            for (change in listOf("replacement", "expiry", "deletion")) {
                val token = database.seedPasswordResetForTest(user.id)
                var replacement: PasswordResetToken? = null
                val hasher =
                    object : PasswordHasher by AlwaysMatchingPasswordHasher {
                        override fun hash(password: PlainTextPassword): PasswordHash {
                            when (change) {
                                "replacement" -> {
                                    replacement = database.seedPasswordResetForTest(user.id)
                                }

                                "expiry" -> {
                                    database.executeSqlScript(
                                        "UPDATE g_password_reset SET created_at = " +
                                            "clock_timestamp() AT TIME ZONE 'UTC' - INTERVAL '16 minutes'",
                                    )
                                }

                                "deletion" -> {
                                    assertEquals(
                                        1,
                                        database.commitTransaction {
                                            PasswordResetsTable.deleteWhere {
                                                PasswordResetsTable.userId eq
                                                    user.id.value
                                            }
                                        },
                                    )
                                }
                            }
                            return AlwaysMatchingPasswordHasher.hash(password)
                        }
                    }
                val failure =
                    assertFailsWith<UserConflict> {
                        ResetPassword(database, hasher).reset(Actor.Anonymous, completion(token))
                    }
                assertEquals("Password reset token is invalid or expired", failure.message)
                assertEquals(before, database.findPasswordUser(user.id))
                replacement?.let { assertEquals(user.id, resets.findUser(it)) }
            }
        }

    @Test
    fun `a profile update or user deletion during hashing cannot be overwritten by a reset`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val oldHash = database.findPasswordUser(user.id)?.passwordHash
            val resets = PasswordResets(database)
            for (deleteUser in listOf(false, true)) {
                val token = database.seedPasswordResetForTest(user.id)
                val hasher =
                    object : PasswordHasher by AlwaysMatchingPasswordHasher {
                        override fun hash(password: PlainTextPassword): PasswordHash {
                            if (deleteUser) {
                                database.commitTransaction { UsersTable.deleteWhere { UsersTable.id eq user.id.value } }
                            } else {
                                UpdateMyEmail(database).update(user.profileActor(), Email("reset.changed@example.org"))
                            }
                            return AlwaysMatchingPasswordHasher.hash(password)
                        }
                    }
                val failure =
                    assertFailsWith<UserConflict> {
                        ResetPassword(database, hasher).reset(Actor.Anonymous, completion(token))
                    }
                assertEquals("User is missing or changed while setting the password", failure.message)
                if (deleteUser) {
                    assertNull(database.findPasswordUser(user.id))
                } else {
                    assertEquals(oldHash, database.findPasswordUser(user.id)?.passwordHash)
                    assertEquals(user.version + 1, database.findPasswordUser(user.id)?.version)
                    assertEquals(Email("reset.changed@example.org"), UserQueries(database).findUser(user.id)?.email)
                    assertEquals(user.id, resets.findUser(token))
                }
            }
        }

    @Test
    fun `failed password persistence restores the consumed token without repeating hashing`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val before = database.findPasswordUser(user.id)
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_reset_password() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected password reset failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_reset_password AFTER UPDATE OF password ON g_user
                FOR EACH ROW EXECUTE FUNCTION reject_reset_password();
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
            val operation = ResetPassword(database, hasher)
            assertFailsWith<SQLException> { operation.reset(Actor.Anonymous, completion(token)) }
            assertEquals(1, hashes)
            assertEquals(before, database.findPasswordUser(user.id))
            assertEquals(user.id, resets.findUser(token))
            database.executeSqlScript("DROP TRIGGER reject_reset_password ON g_user")
            operation.reset(Actor.Anonymous, completion(token))
            assertNull(resets.findUser(token))
            assertEquals(user.version + 1, database.findPasswordUser(user.id)?.version)
        }

    @Test
    fun `two resets hashing against the same token commit exactly one password`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            val hashing = CountDownLatch(2)
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashing.countDown()
                        check(hashing.await(10, TimeUnit.SECONDS))
                        return PasswordHash("{bcrypt}\$test" + password.value)
                    }
                }
            val operation = ResetPassword(database, hasher)
            Executors.newFixedThreadPool(2).use { workers ->
                val attempts =
                    List(2) { index ->
                        workers.submit<PasswordHash?> {
                            val password = PlainTextPassword("replacement password $index")
                            try {
                                operation.reset(
                                    Actor.Anonymous,
                                    PasswordResetCompletion(token, password, password.value),
                                )
                                PasswordHash("{bcrypt}\$test" + password.value)
                            } catch (_: UserConflict) {
                                null
                            }
                        }
                    }
                val winners = attempts.mapNotNull { it.get(15, TimeUnit.SECONDS) }
                assertEquals(1, winners.size)
                assertEquals(winners.single(), database.findPasswordUser(user.id)?.passwordHash)
                assertEquals(user.version + 1, database.findPasswordUser(user.id)?.version)
                assertNull(resets.findUser(token))
            }
        }

    @Test
    fun `reset completion waits for the user before taking its token lock`() =
        withUserDatabase(maximumPoolSize = 2) { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val token = database.seedPasswordResetForTest(user.id)
            Executors.newSingleThreadExecutor().use { worker ->
                val attempt =
                    database.commitTransaction {
                        maxAttempts = 1
                        assertTrue(lockUserIfPresent(user.id))
                        val reset =
                            worker.submit<Unit> {
                                ResetPassword(
                                    database,
                                    AlwaysMatchingPasswordHasher,
                                ).reset(Actor.Anonymous, completion(token))
                            }
                        awaitResetLock("g_user")
                        exec("SET LOCAL lock_timeout = '500ms'")
                        exec("SELECT user_id FROM g_password_reset WHERE user_id = '${user.id.value}' FOR UPDATE") {
                            assertTrue(it.next())
                        }
                        reset
                    }
                attempt.get(10, TimeUnit.SECONDS)
                assertEquals(user.version + 1, database.findPasswordUser(user.id)?.version)
            }
        }

    @Test
    fun `a reset token that expires while waiting for its row lock cannot change the password`() =
        withUserDatabase(maximumPoolSize = 2) { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val before = database.findPasswordUser(user.id)
            val token = database.seedPasswordResetForTest(user.id)
            val hashing = CountDownLatch(1)
            val finishHashing = CountDownLatch(1)
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashing.countDown()
                        check(finishHashing.await(10, TimeUnit.SECONDS))
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            Executors.newSingleThreadExecutor().use { worker ->
                val attempt =
                    worker.submit<Unit> {
                        ResetPassword(database, hasher).reset(Actor.Anonymous, completion(token))
                    }
                try {
                    assertTrue(hashing.await(10, TimeUnit.SECONDS))
                    database.commitTransaction {
                        maxAttempts = 1
                        exec(
                            "UPDATE g_password_reset SET created_at = clock_timestamp() AT TIME ZONE 'UTC' " +
                                "- INTERVAL '14 minutes 58 seconds' WHERE user_id = '${user.id.value}'",
                        )
                        finishHashing.countDown()
                        awaitResetLock()
                        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
                        while (true) {
                            val expired =
                                exec(
                                    "SELECT created_at + INTERVAL '15 minutes' <= " +
                                        "clock_timestamp() AT TIME ZONE 'UTC' " +
                                        "FROM g_password_reset WHERE user_id = '${user.id.value}'",
                                ) { rows ->
                                    rows.next()
                                    rows.getBoolean(1)
                                } == true
                            if (expired) break
                            check(System.nanoTime() < deadline) { "Reset token did not expire" }
                            Thread.sleep(10)
                        }
                    }
                } finally {
                    finishHashing.countDown()
                }
                val failure = assertFailsWith<ExecutionException> { attempt.get(10, TimeUnit.SECONDS) }
                assertIs<UserConflict>(failure.cause)
                assertEquals(before, database.findPasswordUser(user.id))
                assertEquals(1L, database.tableRowCount("g_password_reset"))
            }
        }
}

private fun JdbcTransaction.awaitResetLock(table: String = "g_password_reset") {
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
        check(System.nanoTime() < deadline) { "Password reset did not reach its database lock" }
        Thread.sleep(10)
    }
}

private fun completion(token: PasswordResetToken) =
    PasswordResetCompletion(token, PlainTextPassword("a replacement password"), "a replacement password")

private object UnexpectedResetHasher : PasswordHasher by AlwaysMatchingPasswordHasher {
    override fun hash(password: PlainTextPassword): PasswordHash = error("Unexpected reset hashing")
}
