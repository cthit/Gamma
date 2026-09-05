package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateMyProfileIntegrationTest {
    @Test
    fun `personal edit preserves acceptance year and lock state and rejects stale version`() =
        withUserDatabase { database ->
            database.executeSqlScript("UPDATE g_user SET locked = TRUE, version = NULL WHERE cid = 'jhalpert'")
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val input =
                previous.myProfileUpdate().copy(
                    nick = Nick("New nick"),
                    firstName = FirstName("New first"),
                    lastName = LastName("New last"),
                    language = Language.SV,
                    email = Email("NEW@EXAMPLE.ORG"),
                )
            val operation = UpdateMyProfile(database)
            operation.update(previous.profileActor(), input)
            val committed = queries.findUser(previous.id)
            assertEquals(
                previous.copy(
                    nick = input.nick,
                    firstName = input.firstName,
                    lastName = input.lastName,
                    language = input.language,
                    email = Email("new@example.org"),
                    version = 1,
                ),
                committed,
            )
            assertFailsWith<UserConflict> { operation.update(previous.profileActor(), input) }
            assertEquals(committed, queries.findUser(previous.id))
        }

    @Test
    fun `missing user anonymous actor and missing language are explicit failures`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val input = previous.myProfileUpdate()
            val operation = UpdateMyProfile(database)
            assertFailsWith<AccessDenied> { operation.update(Actor.Anonymous, input.copy(language = null)) }
            assertFailsWith<IllegalArgumentException> {
                operation.update(
                    previous.profileActor(),
                    input.copy(language = null),
                )
            }
            assertFailsWith<UserNotFound> { operation.update(Actor.User(ActorUserId(UUID.randomUUID()), false), input) }
            assertEquals(previous, queries.findUser(previous.id))
        }

    @Test
    fun `duplicate email rejects the whole personal edit`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val other = assertNotNull(queries.findUser(Cid("mscott")))
            val failure =
                assertFailsWith<UserConflict> {
                    UpdateMyProfile(database).update(
                        previous.profileActor(),
                        previous.myProfileUpdate().copy(
                            nick = Nick("Must roll back"),
                            email = Email(other.email.value.uppercase()),
                        ),
                    )
                }
            assertEquals("Email is already in use", failure.message)
            assertEquals(previous, queries.findUser(previous.id))
        }
}
