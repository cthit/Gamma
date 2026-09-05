package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChangeMyPasswordIntegrationTest {
    @Test
    fun `a password change accepts a legacy null version and invalidates the previous password`() =
        withUserDatabase { database ->
            database.executeSqlScript("UPDATE g_user SET version = NULL WHERE cid = 'jhalpert'")
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher = BcryptPasswordHasher(cost = 10)
            ChangeMyPassword(database, hasher).change(user.profileActor(), input)
            val saved = assertNotNull(database.findPasswordUser(user.id))
            assertEquals(1, saved.version)
            assertTrue(hasher.verify(input.newPassword, assertNotNull(saved.passwordHash)))
            assertFalse(hasher.verify(input.currentPassword, saved.passwordHash))
            assertEquals(user.copy(version = 1), UserQueries(database).findUser(user.id))
            assertEquals("MyPasswordChange(<redacted>)", input.toString())
        }

    @Test
    fun `anonymous mismatched and ambient requests are rejected before password work`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher =
                object : PasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash = error("Unexpected password work")

                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean = error("Unexpected password work")

                    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean =
                        error("Unexpected password work")
                }
            val operation = ChangeMyPassword(database, hasher)
            assertFailsWith<AccessDenied> { operation.change(Actor.Anonymous, input) }
            assertFailsWith<UserConflict> {
                operation.change(user.profileActor(), input.copy(confirmedPassword = "different"))
            }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { operation.change(user.profileActor(), input) }
            }
            assertEquals(user, UserQueries(database).findUser(user.id))
        }

    @Test
    fun `wrong missing and unset passwords fail uniformly without hashing a replacement`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            var verifications = 0
            var dummyVerifications = 0
            val hasher =
                object : PasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash = error("Unexpected replacement hash")

                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        verifications++
                        return false
                    }

                    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean {
                        dummyVerifications++
                        return false
                    }
                }
            val operation = ChangeMyPassword(database, hasher)
            val incorrect = assertFailsWith<UserConflict> { operation.change(user.profileActor(), input) }
            val missing =
                assertFailsWith<UserConflict> {
                    operation.change(Actor.User(ActorUserId(UUID.randomUUID())), input)
                }
            database.executeSqlScript("UPDATE g_user SET password = NULL WHERE cid = 'jhalpert'")
            val unset = assertFailsWith<UserConflict> { operation.change(user.profileActor(), input) }
            assertEquals("Incorrect password", incorrect.message)
            assertEquals(incorrect.message, missing.message)
            assertEquals(incorrect.message, unset.message)
            assertEquals(1, verifications)
            assertEquals(2, dummyVerifications)
            assertNull(database.findPasswordUser(user.id)?.passwordHash)
            assertEquals(user.version, database.findPasswordUser(user.id)?.version)
        }

    @Test
    fun `password work releases the only connection and is not repeated when an update rolls back`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val before = assertNotNull(database.findPasswordUser(user.id))
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_password_update() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected password update failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_password_update AFTER UPDATE OF password ON g_user
                FOR EACH ROW EXECUTE FUNCTION reject_password_update();
                """.trimIndent(),
            )
            var verifications = 0
            var hashes = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        verifications++
                        assertTrue(database.ping())
                        return true
                    }

                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        assertTrue(database.ping())
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            assertFailsWith<SQLException> { ChangeMyPassword(database, hasher).change(user.profileActor(), input) }
            assertEquals(before, database.findPasswordUser(user.id))
            assertEquals(user, UserQueries(database).findUser(user.id))
            assertEquals(1, verifications)
            assertEquals(1, hashes)
        }

    @Test
    fun `a profile change or deletion during hashing invalidates the verified credentials`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val oldHash = database.findPasswordUser(user.id)?.passwordHash
            for (deleteUser in listOf(false, true)) {
                val hasher =
                    object : PasswordHasher by AlwaysMatchingPasswordHasher {
                        override fun hash(password: PlainTextPassword): PasswordHash {
                            if (deleteUser) {
                                database.commitTransaction { UsersTable.deleteWhere { UsersTable.id eq user.id.value } }
                            } else {
                                UpdateMyEmail(database).update(user.profileActor(), Email("changed@example.org"))
                            }
                            return AlwaysMatchingPasswordHasher.hash(password)
                        }
                    }
                val failure =
                    assertFailsWith<UserConflict> {
                        ChangeMyPassword(database, hasher).change(user.profileActor(), input)
                    }
                assertEquals("Credentials changed while setting the password", failure.message)
                if (deleteUser) {
                    assertNull(UserQueries(database).findUser(user.id))
                } else {
                    assertEquals(oldHash, database.findPasswordUser(user.id)?.passwordHash)
                    assertEquals(user.version + 1, database.findPasswordUser(user.id)?.version)
                    assertEquals(Email("changed@example.org"), UserQueries(database).findUser(user.id)?.email)
                }
            }
        }

    @Test
    fun `two changes verified against the same credentials commit exactly one replacement`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hashing = CountDownLatch(2)
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashing.countDown()
                        check(hashing.await(10, TimeUnit.SECONDS))
                        return PasswordHash("{bcrypt}\$test" + password.value)
                    }
                }
            val operation = ChangeMyPassword(database, hasher)
            Executors.newFixedThreadPool(2).use { workers ->
                val attempts =
                    List(2) { index ->
                        workers.submit<PasswordHash?> {
                            val password = PlainTextPassword("replacement password $index")
                            try {
                                operation.change(
                                    user.profileActor(),
                                    input.copy(newPassword = password, confirmedPassword = password.value),
                                )
                                PasswordHash("{bcrypt}\$test" + password.value)
                            } catch (failure: UserConflict) {
                                assertEquals("Credentials changed while setting the password", failure.message)
                                null
                            }
                        }
                    }
                val winners = attempts.mapNotNull { it.get(15, TimeUnit.SECONDS) }
                assertEquals(1, winners.size)
                assertEquals(winners.single(), database.findPasswordUser(user.id)?.passwordHash)
                assertEquals(user.version + 1, database.findPasswordUser(user.id)?.version)
            }
        }

    private val input =
        MyPasswordChange(
            PlainTextPassword("password1337"),
            PlainTextPassword("new correct horse password"),
            "new correct horse password",
        )
}
