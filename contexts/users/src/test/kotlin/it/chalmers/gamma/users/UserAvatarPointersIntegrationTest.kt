package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class UserAvatarPointersIntegrationTest {
    @Test
    fun `user can clear and recover the current avatar pointer`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val user = run { checkNotNull(queries.findUser(Cid("jhalpert"))) }
            val changes = UserAvatarPointers(database)
            val operation = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000005"))
            val avatar = StoredUserAvatar("${operation.value}.webp")

            run {
                changes.replaceAvatar(user.id, operation, avatar, expectedAvatar = null)

                assertEquals(avatar, changes.currentAvatar(user.id))
                changes.clearAvatar(user.id, avatar)
                assertNull(queries.findUser(user.id)?.avatarUri)
                changes.clearAvatar(user.id, expectedAvatar = null)
            }
        }

    @Test
    fun `administrator can clear another users avatar`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = run { checkNotNull(queries.findUser(Cid(FIXTURE_ADMINISTRATOR_CID))) }
            val target = run { checkNotNull(queries.findUser(Cid("pbeesly"))) }
            val changes = UserAvatarPointers(database)
            val operation = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000006"))
            val avatar = StoredUserAvatar("${operation.value}.webp")

            run {
                changes.replaceAvatar(target.id, operation, avatar, expectedAvatar = null)

                assertEquals(avatar, changes.currentAvatarAsAdministrator(administrator.id, target.id))
                changes.clearAvatarAsAdministrator(administrator.id, target.id, avatar)
                assertNull(queries.findUser(target.id)?.avatarUri)
            }
        }

    @Test
    fun `administrator avatar clearing rejects an ordinary user`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val ordinaryUser = run { checkNotNull(queries.findUser(Cid("jhalpert"))) }
            val targetUser = run { checkNotNull(queries.findUser(Cid("pbeesly"))) }

            assertFailsWith<AccessDenied> {
                run {
                    UserAvatarPointers(database)
                        .clearAvatarAsAdministrator(ordinaryUser.id, targetUser.id, expectedAvatar = null)
                }
            }

            assertFailsWith<AccessDenied> {
                run {
                    UserAvatarPointers(database)
                        .currentAvatarAsAdministrator(ordinaryUser.id, targetUser.id)
                }
            }
        }

    @Test
    fun `clear conflicts rather than removing an avatar changed after capture`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val userId = run { checkNotNull(queries.findUser(Cid("mscott"))).id }
            val changes = UserAvatarPointers(database)
            val firstOperation = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000007"))
            val secondOperation = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000008"))
            val firstAvatar = StoredUserAvatar("${firstOperation.value}.webp")
            val secondAvatar = StoredUserAvatar("${secondOperation.value}.webp")

            run {
                changes.replaceAvatar(userId, firstOperation, firstAvatar, expectedAvatar = null)
                val capturedAvatar = changes.currentAvatar(userId)
                changes.replaceAvatar(userId, secondOperation, secondAvatar, firstAvatar)

                assertFailsWith<UserConflict> {
                    changes.clearAvatar(userId, capturedAvatar)
                }
                assertEquals(secondAvatar, changes.currentAvatar(userId))
            }
        }

    @Test
    fun `avatar replacement updates the pointer and returns the previous object`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val userId = run { checkNotNull(queries.findUser(Cid("mscott"))).id }
            val changes = UserAvatarPointers(database)
            val firstOperation = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000002"))
            val secondOperation = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000003"))
            val firstAvatar = StoredUserAvatar("${firstOperation.value}.webp")
            val secondAvatar = StoredUserAvatar("${secondOperation.value}.webp")

            run {
                assertNull(changes.replaceAvatar(userId, firstOperation, firstAvatar, expectedAvatar = null))
                assertEquals(firstAvatar, changes.currentAvatar(userId))
                assertEquals(firstAvatar, changes.replaceAvatar(userId, secondOperation, secondAvatar, firstAvatar))
                assertEquals(secondAvatar, changes.currentAvatar(userId))
                assertEquals(secondAvatar.uri, queries.findUser(userId)?.avatarUri)
            }
        }
}
