package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import java.sql.SQLException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateUserIntegrationTest {
    @Test
    fun `creation releases its connection for hashing and atomically consumes reservations`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val input = newUser()
            val activations = ActivationCodes(database)
            activations.allow(input.cid)
            val token = database.seedActivationForTest(input.cid)
            var hashes = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        assertTrue(database.ping())
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            val userId = CreateUser(database, hasher).create(administrator, input)
            val saved = assertNotNull(UserQueries(database).findUser(userId))
            assertEquals(input.cid, saved.cid)
            assertEquals(input.nick, saved.nick)
            assertEquals(input.firstName, saved.firstName)
            assertEquals(input.lastName, saved.lastName)
            assertEquals(input.acceptanceYear, saved.acceptanceYear)
            assertEquals(input.language, saved.language)
            assertEquals(Email("new.student@example.org"), saved.email)
            assertEquals(0, saved.version)
            assertEquals(false, saved.locked)
            assertEquals(
                AlwaysMatchingPasswordHasher.hash(input.password),
                database.findPasswordUser(userId)?.passwordHash,
            )
            assertEquals(1, hashes)
            assertEquals(false, activations.allowedCids().contains(input.cid))
            assertNull(activations.findCid(token))
        }

    @Test
    fun `unauthorized creation is denied before hashing`() =
        withUserDatabase { database ->
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash = error("Unexpected hashing")
                }
            val member = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val operation = CreateUser(database, hasher)
            assertFailsWith<AccessDenied> { operation.create(member.profileActor(isAdministrator = true), newUser()) }
            assertFailsWith<AccessDenied> { operation.create(Actor.Anonymous, newUser()) }
            assertNull(UserQueries(database).findUser(newUser().cid))
        }

    @Test
    fun `demotion during hashing rejects creation and leaves reservations usable`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val input = newUser()
            val activations = ActivationCodes(database)
            activations.allow(input.cid)
            val token = database.seedActivationForTest(input.cid)
            val member = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        UserAccessFlags(database).replace(
                            administrator,
                            UserAccessFlagKind.ADMINISTRATOR,
                            setOf(member.id),
                        )
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            assertFailsWith<AccessDenied> { CreateUser(database, hasher).create(administrator, input) }
            assertNull(UserQueries(database).findUser(input.cid))
            assertTrue(activations.allowedCids().contains(input.cid))
            assertEquals(input.cid, activations.findCid(token))
        }

    @Test
    fun `failed insertion rolls back reservation deletion and retries without rehashing`() =
        withUserDatabase { database ->
            val input = newUser()
            val activations = ActivationCodes(database)
            activations.allow(input.cid)
            val token = database.seedActivationForTest(input.cid)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_test_user() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected user insertion failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_test_user BEFORE INSERT ON g_user
                FOR EACH ROW EXECUTE FUNCTION reject_test_user();
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
            assertFailsWith<SQLException> { CreateUser(database, hasher).create(administrator, input) }
            assertEquals(1, hashes)
            assertNull(UserQueries(database).findUser(input.cid))
            assertTrue(activations.allowedCids().contains(input.cid))
            assertEquals(input.cid, activations.findCid(token))
        }

    @Test
    fun `existing CID and case insensitive email produce domain conflicts`() =
        withUserDatabase { database ->
            val existing = assertNotNull(UserQueries(database).findUser(Cid("mscott")))
            val operation = CreateUser(database, AlwaysMatchingPasswordHasher)
            val cidFailure =
                assertFailsWith<UserConflict> {
                    operation.create(administrator, newUser().copy(cid = existing.cid))
                }
            assertEquals("CID is already in use", cidFailure.message)
            val emailFailure =
                assertFailsWith<UserConflict> {
                    operation.create(administrator, newUser().copy(email = Email(existing.email.value.uppercase())))
                }
            assertEquals("Email is already in use", emailFailure.message)
            assertNull(UserQueries(database).findUser(newUser().cid))
        }

    @Test
    fun `competing creations that hash together produce one identity and one CID conflict`() =
        withUserDatabase { database ->
            val hashing = CountDownLatch(2)
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashing.countDown()
                        check(hashing.await(10, TimeUnit.SECONDS))
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            val operation = CreateUser(database, hasher)
            Executors.newFixedThreadPool(2).use { workers ->
                val attempts =
                    List(2) {
                        workers.submit<UserId?> {
                            try {
                                operation.create(administrator, newUser())
                            } catch (failure: UserConflict) {
                                assertEquals("CID is already in use", failure.message)
                                null
                            }
                        }
                    }
                val winners = attempts.mapNotNull { it.get(15, TimeUnit.SECONDS) }
                assertEquals(1, winners.size)
                assertEquals(winners.single(), UserQueries(database).findUser(newUser().cid)?.id)
            }
        }

    private val administrator = Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value), true)

    private fun newUser() =
        NewUser(
            cid = Cid("newstudent"),
            nick = Nick("New student"),
            firstName = FirstName("New"),
            lastName = LastName("Student"),
            acceptanceYear = AcceptanceYear.of(2021, 2026),
            language = Language.EN,
            email = Email("NEW.STUDENT@EXAMPLE.ORG"),
            password = PlainTextPassword("password1337"),
        )
}
