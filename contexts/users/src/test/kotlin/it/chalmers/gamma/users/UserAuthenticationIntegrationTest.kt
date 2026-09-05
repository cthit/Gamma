package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.update
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UserAuthenticationIntegrationTest {
    @Test
    fun `password changed during verification cannot authorize a login`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        ChangeMyPassword(database, AlwaysMatchingPasswordHasher).change(
                            user.profileActor(),
                            MyPasswordChange(
                                password,
                                PlainTextPassword("the replacement password"),
                                "the replacement password",
                            ),
                        )
                        return true
                    }
                }
            val authentication = UserAuthentication(database, hasher)
            assertNull(authentication.authenticate(user.cid, PlainTextPassword("password1337")))
            assertEquals(
                AlwaysMatchingPasswordHasher.hash(PlainTextPassword("the replacement password")),
                database.findPasswordUser(user.id)?.passwordHash,
            )
        }

    @Test
    fun `email changed during verification cannot authorize the old email identifier`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        database.commitTransaction {
                            UsersTable.update({ UsersTable.id eq user.id.value }) { it[email] = "changed@example.org" }
                        }
                        return true
                    }
                }
            assertNull(UserAuthentication(database, hasher).authenticate(user.email, PlainTextPassword("password1337")))
        }

    @Test
    fun `authentication rejects an enclosing transaction before verifying a password`() =
        withUserDatabase { database ->
            var verifications = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        verifications += 1
                        return true
                    }
                }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    UserAuthentication(
                        database,
                        hasher,
                    ).authenticate(Cid("jhalpert"), PlainTextPassword("password1337"))
                }
            }
            assertEquals(0, verifications)
        }

    @Test
    fun `CID email and user ID authenticate with the real password hash`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val authentication = UserAuthentication(database, BcryptPasswordHasher(cost = 10))
            for (identifier in listOf(user.cid, Email(user.email.value.uppercase()), user.id)) {
                val result = assertNotNull(authentication.authenticate(identifier, PlainTextPassword("password1337")))
                assertEquals(user.id, result.userId)
                assertEquals(user.nick, result.nick)
                assertFalse(result.administrator)
                assertEquals("AuthenticatedUser(<redacted>)", result.toString())
            }
            assertNull(authentication.authenticate(user.cid, PlainTextPassword("an incorrect password")))
        }

    @Test
    fun `missing and credentialless users perform dummy work outside a transaction`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            database.commitTransaction { UsersTable.update({ UsersTable.id eq user.id.value }) { it[password] = null } }
            var dummyVerifications = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verifyAgainstDummy(password: PlainTextPassword): Boolean {
                        assertNull(TransactionManager.currentOrNull())
                        assertTrue(database.ping())
                        dummyVerifications += 1
                        return true // Even an adapter reporting a match cannot authenticate a missing credential.
                    }

                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean = error("Unexpected real hash")
                }
            val authentication = UserAuthentication(database, hasher)
            assertNull(authentication.authenticate(Cid("missinguser"), PlainTextPassword("password1337")))
            assertNull(authentication.authenticate(user.id, PlainTextPassword("password1337")))
            assertEquals(2, dummyVerifications)
        }

    @Test
    fun `account locked during verification cannot authorize a login`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        database.commitTransaction {
                            UsersTable.update({ UsersTable.id eq user.id.value }) {
                                it[locked] =
                                    true
                            }
                        }
                        return true
                    }
                }
            assertNull(UserAuthentication(database, hasher).authenticate(user.cid, PlainTextPassword("password1337")))
        }

    @Test
    fun `account deleted during verification cannot authorize a login`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        database.commitTransaction { UsersTable.deleteWhere { UsersTable.id eq user.id.value } }
                        return true
                    }
                }
            assertNull(UserAuthentication(database, hasher).authenticate(user.cid, PlainTextPassword("password1337")))
        }

    @Test
    fun `login returns current nick and administrator authority after verification`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("mscott")))
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        assertNull(TransactionManager.currentOrNull())
                        assertTrue(database.ping())
                        database.commitTransaction {
                            UsersTable.update({ UsersTable.id eq user.id.value }) {
                                it[nick] = "New display name"
                                it[version] = user.version + 1
                            }
                            AdminUsersTable.deleteWhere { AdminUsersTable.userId eq user.id.value }
                        }
                        return true
                    }
                }
            val result =
                assertNotNull(
                    UserAuthentication(database, hasher).authenticate(user.cid, PlainTextPassword("password1337")),
                )
            assertEquals(Nick("New display name"), result.nick)
            assertFalse(result.administrator)
        }

    @Test
    fun `session access observes current administrator and account state and owns its read`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val authentication = UserAuthentication(database, AlwaysMatchingPasswordHasher)
            assertEquals(SessionAccess(false, false), authentication.sessionAccess(user.id))
            database.commitTransaction {
                AdminUsersTable.insert {
                    it[userId] = user.id.value
                    it[createdAt] = userPersistenceTime()
                }
                UsersTable.update({ UsersTable.id eq user.id.value }) { it[locked] = true }
            }
            assertEquals(SessionAccess(true, true), authentication.sessionAccess(user.id))
            database.commitTransaction {
                AdminUsersTable.deleteWhere { AdminUsersTable.userId eq user.id.value }
                UsersTable.update({ UsersTable.id eq user.id.value }) { it[locked] = false }
            }
            assertEquals(SessionAccess(false, false), authentication.sessionAccess(user.id))
            assertNull(authentication.sessionAccess(UserId(UUID.randomUUID())))
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    authentication.sessionAccess(
                        user.id,
                    )
                }
            }
        }

    @Test
    fun `verification cancellation and interruption propagate unchanged`() =
        withUserDatabase { database ->
            for (failure in listOf(
                CancellationException("verification cancelled"),
                InterruptedException("verification interrupted"),
            )) {
                val hasher =
                    object : PasswordHasher by AlwaysMatchingPasswordHasher {
                        override fun verify(
                            password: PlainTextPassword,
                            hash: PasswordHash,
                        ): Boolean = throw failure
                    }
                val result =
                    kotlin.test.assertFails {
                        UserAuthentication(
                            database,
                            hasher,
                        ).authenticate(Cid("jhalpert"), PlainTextPassword("password1337"))
                    }
                assertSame(failure, result)
            }
        }

    @Test
    fun `a final database read retry does not repeat password verification`() {
        PostgresTestEnvironment().use { postgres ->
            var armed = false
            var failures = 0
            var verifications = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (armed) {
                            armed = false
                            failures += 1
                            throw SQLException("read temporarily unavailable")
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val hasher =
                    object : PasswordHasher by AlwaysMatchingPasswordHasher {
                        override fun verify(
                            password: PlainTextPassword,
                            hash: PasswordHash,
                        ): Boolean {
                            verifications += 1
                            armed = true
                            return true
                        }
                    }
                val result =
                    assertNotNull(
                        UserAuthentication(
                            database,
                            hasher,
                        ).authenticate(Cid("mscott"), PlainTextPassword("password1337")),
                    )
                assertTrue(result.administrator)
                assertEquals(1, failures)
                assertEquals(1, verifications)
            }
        }
    }
}
