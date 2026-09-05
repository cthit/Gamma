package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateMyEmailIntegrationTest {
    @Test
    fun `email-only edit preserves a preceding profile edit and invalidates older full forms`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val profiles = UpdateMyProfile(database)
            profiles.update(previous.profileActor(), previous.myProfileUpdate().copy(nick = Nick("Fresh nick")))
            val beforeEmail = assertNotNull(queries.findUser(previous.id))
            UpdateMyEmail(database).update(previous.profileActor(), Email("ONLY.EMAIL@EXAMPLE.ORG"))
            val committed = queries.findUser(previous.id)
            assertEquals(
                beforeEmail.copy(email = Email("only.email@example.org"), version = beforeEmail.version + 1),
                committed,
            )
            assertFailsWith<UserConflict> { profiles.update(previous.profileActor(), beforeEmail.myProfileUpdate()) }
            assertEquals(committed, queries.findUser(previous.id))
        }

    @Test
    fun `duplicate email leaves email and version unchanged`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val other = assertNotNull(queries.findUser(Cid("mscott")))
            val failure =
                assertFailsWith<UserConflict> {
                    UpdateMyEmail(database).update(previous.profileActor(), Email(other.email.value.uppercase()))
                }
            assertEquals("Email is already in use", failure.message)
            assertEquals(previous, queries.findUser(previous.id))
        }

    @Test
    fun `email edit retains incomplete-profile rejection and actor and missing-user checks`() =
        withUserDatabase { database ->
            database.executeSqlScript("UPDATE g_user SET language = NULL WHERE cid = 'jhalpert'")
            val queries = UserQueries(database)
            val previous = assertNotNull(queries.findUser(Cid("jhalpert")))
            val email = Email("replacement@example.org")
            val operation = UpdateMyEmail(database)
            assertFailsWith<IllegalArgumentException> { operation.update(previous.profileActor(), email) }
            assertFailsWith<AccessDenied> { operation.update(Actor.Anonymous, email) }
            assertFailsWith<UserNotFound> { operation.update(Actor.User(ActorUserId(UUID.randomUUID()), false), email) }
            assertEquals(previous, queries.findUser(previous.id))
        }
}
