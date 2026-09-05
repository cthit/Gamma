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
    fun `saves group metadata and replaces memberships together`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.findGroup(existingGroupId))
            val input = edit(original)
            UpdateGroup(database).update(groupAdministrator, input)

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
            val operation = UpdateGroup(database)
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

            assertFails { UpdateGroup(database).update(groupAdministrator, input) }
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

            assertFails { UpdateGroup(database).update(groupAdministrator, edit(original)) }
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
                UpdateGroup(database).update(ordinaryGroupUser, edit(original))
            }
            assertEquals(original, queries.findGroup(original.id))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, original.id)
                },
            )
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
