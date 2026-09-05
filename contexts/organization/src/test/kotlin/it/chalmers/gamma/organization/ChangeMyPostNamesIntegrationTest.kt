package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class ChangeMyPostNamesIntegrationTest {
    @Test
    fun `changes only the signed in members submitted post names`() =
        withGroupDatabase { database, queries ->
            val posts = queries.listPosts().take(2)
            val memberships =
                posts.map { groupMembership.copy(postId = it.id) } +
                    groupMembership.copy(userId = UserId(groupAdministrator.userId.value))
            val groupId =
                CreateGroup(database, organizationAccess(database)).create(
                    groupAdministrator,
                    NewGroup(OrganizationName("personal-posts"), PrettyName("Personal posts"), existingSuperGroupId),
                    memberships,
                )
            val before = database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, groupId) }
            val names =
                listOf(
                    PersonalPostName(posts[0].id, UnofficialPostName("My chairman")),
                    PersonalPostName(posts[1].id, UnofficialPostName(null)),
                )

            ChangeMyPostNames(database).change(ordinaryGroupUser, groupId, names)

            val after = database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, groupId) }
            for (membership in before) {
                val expectedName =
                    if (membership.userId.value == ordinaryGroupUser.userId.value) {
                        names.single { it.postId == membership.postId }.name
                    } else {
                        membership.unofficialPostName
                    }
                assertEquals(
                    membership.copy(unofficialPostName = expectedName),
                    after.single {
                        it.userId == membership.userId && it.postId == membership.postId
                    },
                )
            }
        }

    @Test
    fun `later denied post rolls back earlier changes even for an administrator`() =
        withGroupDatabase { database, queries ->
            val posts = queries.listPosts().take(2)
            val groupId =
                CreateGroup(database, organizationAccess(database)).create(
                    groupAdministrator,
                    NewGroup(OrganizationName("denied-posts"), PrettyName("Denied posts"), existingSuperGroupId),
                    listOf(
                        groupMembership.copy(postId = posts[0].id),
                        groupMembership.copy(userId = UserId(groupAdministrator.userId.value), postId = posts[1].id),
                    ),
                )
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, groupId) }
            val actor = ordinaryGroupUser.copy(isAdministrator = true)

            assertFailsWith<AccessDenied> {
                ChangeMyPostNames(database).change(
                    actor,
                    groupId,
                    listOf(
                        PersonalPostName(posts[0].id, UnofficialPostName("Must roll back")),
                        PersonalPostName(posts[1].id, UnofficialPostName("Another member")),
                    ),
                )
            }
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, groupId) },
            )
        }

    @Test
    fun `SQL failure rolls back all names in the submitted form`() =
        withGroupDatabase { database, queries ->
            val posts = queries.listPosts().take(2)
            val groupId =
                CreateGroup(database, organizationAccess(database)).create(
                    groupAdministrator,
                    NewGroup(OrganizationName("failed-posts"), PrettyName("Failed posts"), existingSuperGroupId),
                    posts.map { groupMembership.copy(postId = it.id) },
                )
            val before = database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, groupId) }
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_personal_name() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF NEW.unofficial_post_name = 'Rejected name' THEN
                        RAISE EXCEPTION 'forced name persistence failure';
                    END IF;
                    RETURN NEW;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_personal_name BEFORE UPDATE ON g_membership
                    FOR EACH ROW EXECUTE FUNCTION reject_personal_name();
                """.trimIndent(),
            )

            assertFails {
                ChangeMyPostNames(database).change(
                    ordinaryGroupUser,
                    groupId,
                    listOf(
                        PersonalPostName(posts[0].id, UnofficialPostName("Must roll back")),
                        PersonalPostName(posts[1].id, UnofficialPostName("Rejected name")),
                    ),
                )
            }
            assertEquals(
                before,
                database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, groupId) },
            )
        }

    @Test
    fun `anonymous callers cannot submit personal names`() =
        withGroupDatabase { database, queries ->
            val before =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, existingGroupId) }
            assertFailsWith<AccessDenied> {
                ChangeMyPostNames(database).change(Actor.Anonymous, existingGroupId, emptyList())
            }
            assertEquals(
                before,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, existingGroupId)
                },
            )
        }
}
