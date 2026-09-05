package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UpdateUserIntegrationTest {
    @Test
    fun `administrator updates submitted fields and accepts legacy null version without changing other state`() =
        withUserDatabase { database ->
            database.executeSqlScript(
                "UPDATE g_user SET version = NULL, language = NULL, locked = NULL WHERE cid = 'jhalpert'",
            )
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val administrator = assertNotNull(queries.findUser(Cid("mscott")))
            val update =
                previous.userUpdate().copy(
                    nick = Nick("Edited"),
                    firstName = FirstName("New first"),
                    lastName = LastName("New last"),
                    acceptanceYear = AcceptanceYear.of(2021, 2026),
                    email = Email("EDITED@EXAMPLE.ORG"),
                )
            UpdateUser(database).update(administrator.profileActor(), update)

            val current = assertNotNull(queries.findUser(previous.id))
            assertEquals(
                previous.copy(
                    nick = update.nick,
                    firstName = update.firstName,
                    lastName = update.lastName,
                    acceptanceYear = update.acceptanceYear,
                    email = Email("edited@example.org"),
                    version = 1,
                ),
                current,
            )
            database.commitTransaction(readOnly = true) {
                assertNull(
                    UsersTable.selectAll().where { UsersTable.id eq previous.id.value }.single()[UsersTable.locked],
                )
            }
            assertEquals("UserUpdate(<redacted>)", update.toString())
        }

    @Test
    fun `stale and missing edits do not change any profile fields`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = assertNotNull(queries.findUser(Cid("mscott"))).profileActor()
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val update = previous.userUpdate().copy(nick = Nick("First edit"))
            val operation = UpdateUser(database)
            operation.update(administrator, update)
            val committed = queries.findUser(previous.id)

            assertFailsWith<UserConflict> { operation.update(administrator, update.copy(nick = Nick("Stale edit"))) }
            assertFailsWith<UserConflict> {
                operation.update(
                    administrator,
                    update.copy(userId = UserId(UUID.randomUUID())),
                )
            }
            assertEquals(committed, queries.findUser(previous.id))
        }

    @Test
    fun `database authority precedes target lookup even when cached administrator flag is true`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val member = assertNotNull(queries.findUser(Cid("jhalpert")))
            val input = member.userUpdate().copy(userId = UserId(UUID.randomUUID()))
            val operation = UpdateUser(database)
            assertFailsWith<AccessDenied> { operation.update(member.profileActor(isAdministrator = true), input) }
            assertFailsWith<AccessDenied> { operation.update(Actor.Anonymous, input) }

            database.executeSqlScript("DELETE FROM g_admin_user")
            val demoted = Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value), true)
            assertFailsWith<AccessDenied> { operation.update(demoted, member.userUpdate()) }
            assertEquals(member, queries.findUser(member.id))
        }

    @Test
    fun `duplicate email rolls back every submitted field and version`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = assertNotNull(queries.findUser(Cid("mscott")))
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val failure =
                assertFailsWith<UserConflict> {
                    UpdateUser(database).update(
                        administrator.profileActor(),
                        previous.userUpdate().copy(
                            nick = Nick("Must roll back"),
                            email = Email(administrator.email.value.uppercase()),
                        ),
                    )
                }
            assertEquals("Email is already in use", failure.message)
            assertEquals(previous, queries.findUser(previous.id))
        }

    @Test
    fun `competing edits produce one committed profile and one conflict`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = assertNotNull(queries.findUser(Cid("mscott"))).profileActor()
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val start = CountDownLatch(1)
            val operation = UpdateUser(database)
            Executors.newFixedThreadPool(2).use { workers ->
                val attempts =
                    listOf("One", "Two").map { name ->
                        workers.submit<String?> {
                            check(start.await(10, TimeUnit.SECONDS))
                            try {
                                operation.update(administrator, previous.userUpdate().copy(nick = Nick(name)))
                                name
                            } catch (_: UserConflict) {
                                null
                            }
                        }
                    }
                start.countDown()
                val winners = attempts.mapNotNull { it.get(10, TimeUnit.SECONDS) }
                assertEquals(1, winners.size)
                val current = assertNotNull(queries.findUser(previous.id))
                assertEquals(winners.single(), current.nick.value)
                assertEquals(previous.version + 1, current.version)
            }
        }
}
