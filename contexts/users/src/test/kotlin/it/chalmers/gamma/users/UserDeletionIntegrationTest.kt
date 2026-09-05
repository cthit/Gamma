package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserDeletionIntegrationTest {
    @Test
    fun `participant returns the locked avatar and deletion rolls back with its caller`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val user = assertNotNull(queries.findUser(Cid("jhalpert")))
            val participant = UserDeletion(database, AlwaysMatchingPasswordHasher)
            val operation = UserAvatarOperationId.generate()
            val avatar = StoredUserAvatar("${operation.value}.png")
            UserAvatarPointers(database).replaceAvatar(user.id, operation, avatar, null)
            assertFailsWith<IllegalArgumentException> {
                database.commitTransaction {
                    val target = participant.lockForAdministratorDeletion(this, administrator, user.id)
                    assertEquals(avatar.uri, target.avatarUri)
                    participant.deleteIn(this, target)
                    throw IllegalArgumentException("caller rejected deletion")
                }
            }
            assertEquals(avatar.uri, queries.findUser(user.id)?.avatarUri)
            database.commitTransaction {
                val target = participant.lockForAdministratorDeletion(this, administrator, user.id)
                participant.deleteIn(this, target)
            }
            assertNull(queries.findUser(user.id))
        }

    @Test
    fun `locked deletion authorization cannot be reused in a different transaction`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val user = assertNotNull(queries.findUser(Cid("jhalpert")))
            val participant = UserDeletion(database, AlwaysMatchingPasswordHasher)
            val oldTarget =
                database.commitTransaction {
                    participant.lockForAdministratorDeletion(this, administrator, user.id)
                }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { participant.deleteIn(this, oldTarget) }
            }
            assertNotNull(queries.findUser(user.id))
            assertEquals("LockedUserDeletion(<redacted>)", oldTarget.toString())
        }

    private companion object {
        val administrator = Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value), false)
    }
}
