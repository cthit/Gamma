package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateGroupIntegrationTest {
    @Test
    fun `editing a legacy null version retains empty image pointers`() =
        withGroupDatabase { database, queries ->
            database.executeSqlScript(
                """
                UPDATE g_group SET version = NULL WHERE group_id = '${existingGroupId.value}';
                INSERT INTO g_group_images_uri (created_at, updated_at, group_id, avatar_uri, banner_uri, version)
                VALUES (NOW(), NOW(), '${existingGroupId.value}', NULL, NULL, NULL);
                """.trimIndent(),
            )
            val original = assertNotNull(queries.findGroup(existingGroupId))
            assertEquals(0, original.version)
            assertEquals(null, original.avatarUri)
            assertEquals(null, original.bannerUri)
            UpdateGroup(database, organizationAccess(database)).update(groupAdministrator, edit(original))
            val saved = assertNotNull(queries.findGroup(existingGroupId))
            assertEquals(1, saved.version)
            assertEquals(null, saved.avatarUri)
            assertEquals(null, saved.bannerUri)
        }

    @Test
    fun `saves group metadata and replaces memberships together`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val input = edit(original)
            UpdateGroup(database, organizationAccess(database)).update(groupAdministrator, input)

            val updated = assertNotNull(queries.findGroup(original.id))
            assertEquals(input.name, updated.name)
            assertEquals(input.prettyName, updated.prettyName)
            assertEquals(input.superGroupId, updated.superGroup.id)
            assertEquals(original.version + 1, updated.version)
            assertEquals(
                listOf(
                    Membership(
                        groupMembership.userId,
                        original.id,
                        groupMembership.postId,
                        groupMembership.unofficialPostName,
                    ),
                ),
                database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, original.id) },
            )
        }

    @Test
    fun `stale edit cannot overwrite metadata or memberships`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val operation = UpdateGroup(database, organizationAccess(database))
            operation.update(groupAdministrator, edit(original))
            val committed = queries.findGroup(original.id)
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, original.id) }

            assertFailsWith<OrganizationConflict> {
                operation.update(groupAdministrator, edit(original).copy(memberships = emptyList()))
            }
            assertEquals(committed, queries.findGroup(original.id))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, original.id)
                },
            )
        }

    @Test
    fun `membership insert failure rolls back metadata deletion and preceding inserts`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, original.id) }
            val input =
                edit(original).copy(
                    memberships = listOf(groupMembership, groupMembership.copy(userId = UserId.generate())),
                )

            assertFails { UpdateGroup(database, organizationAccess(database)).update(groupAdministrator, input) }
            assertEquals(original, queries.findGroup(original.id))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, original.id)
                },
            )
        }

    @Test
    fun `membership deletion failure rolls back metadata`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, original.id) }
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_membership_replacement() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    RAISE EXCEPTION 'forced membership persistence failure';
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_membership_replacement
                    BEFORE DELETE ON g_membership
                    FOR EACH ROW EXECUTE FUNCTION reject_membership_replacement();
                """.trimIndent(),
            )

            assertFails {
                UpdateGroup(
                    database,
                    organizationAccess(database),
                ).update(groupAdministrator, edit(original))
            }
            assertEquals(original, queries.findGroup(original.id))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, original.id)
                },
            )
        }

    @Test
    fun `denies an ordinary user before changing group state`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, original.id) }
            assertFailsWith<AccessDenied> {
                UpdateGroup(database, organizationAccess(database)).update(ordinaryGroupUser, edit(original))
            }
            assertEquals(original, queries.findGroup(original.id))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, original.id)
                },
            )
        }

    @Test
    fun `duplicate user and post rejects the entire edit even with different unofficial names`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, original.id) }
            val duplicate = groupMembership.copy(unofficialPostName = UnofficialPostName("Another name"))
            assertFailsWith<OrganizationConflict> {
                UpdateGroup(database, organizationAccess(database)).update(
                    groupAdministrator,
                    edit(original).copy(memberships = listOf(groupMembership, duplicate)),
                )
            }
            assertEquals(original, queries.findGroup(original.id))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, original.id)
                },
            )
        }

    @Test
    fun `a user can hold different posts and users can share a post while edits retain and remove assignments`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val otherPost = queries.listPosts().first { it.id != groupMembership.postId }
            val secondPost = groupMembership.copy(postId = otherPost.id)
            val secondUser = groupMembership.copy(userId = UserId(groupAdministrator.userId.value))
            val operation = UpdateGroup(database, organizationAccess(database))
            val assignments = listOf(groupMembership, secondPost, secondUser)
            operation.update(groupAdministrator, edit(original).copy(memberships = assignments))
            assertEquals(
                3,
                database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, original.id) }.size,
            )
            val saved = assertNotNull(queries.findGroup(original.id))
            operation.update(groupAdministrator, edit(saved).copy(memberships = listOf(groupMembership, secondUser)))
            val remaining =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, original.id) }
            assertEquals(setOf(groupMembership.userId, secondUser.userId), remaining.map { it.userId }.toSet())
            assertEquals(setOf(groupMembership.postId), remaining.map { it.postId }.toSet())
        }

    private fun edit(group: Group) =
        GroupUpdate(
            group.id,
            group.version,
            OrganizationName("updated-group"),
            PrettyName("Updated group"),
            existingSuperGroupId,
            listOf(groupMembership),
        )
}
