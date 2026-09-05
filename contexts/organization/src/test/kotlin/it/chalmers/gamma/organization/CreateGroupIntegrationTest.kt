package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreateGroupIntegrationTest {
    private val input =
        NewGroup(OrganizationName("operation-group"), PrettyName("Operation group"), existingSuperGroupId)

    @Test
    fun `creates the group and initial memberships before returning`() =
        withGroupDatabase { database, queries ->
            val id = CreateGroup(database).create(groupAdministrator, input, listOf(groupMembership))

            val group = assertNotNull(queries.findGroup(id))
            assertEquals(input.name, group.name)
            assertEquals(input.prettyName, group.prettyName)
            assertEquals(input.superGroupId, group.superGroup.id)
            assertEquals(0, group.version)
            assertEquals(
                listOf(
                    Membership(groupMembership.userId, id, groupMembership.postId, groupMembership.unofficialPostName),
                ),
                database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, id) },
            )
        }

    @Test
    fun `denies an ordinary user before creating anything`() =
        withGroupDatabase { database, queries ->
            val before = queries.listGroups()
            assertFailsWith<AccessDenied> {
                CreateGroup(database).create(ordinaryGroupUser, input, emptyList())
            }
            assertEquals(before, queries.listGroups())
        }

    @Test
    fun `missing parent is rejected without creating a group`() =
        withGroupDatabase { database, queries ->
            val before = queries.listGroups()
            assertFailsWith<OrganizationNotFound> {
                CreateGroup(database).create(
                    groupAdministrator,
                    input.copy(superGroupId = SuperGroupId.generate()),
                    emptyList(),
                )
            }
            assertEquals(before, queries.listGroups())
        }

    @Test
    fun `membership failure rolls back the group and preceding memberships`() =
        withGroupDatabase { database, queries ->
            val before = queries.listGroups()
            val invalidMember = groupMembership.copy(userId = UserId.generate())
            assertFails {
                CreateGroup(database).create(groupAdministrator, input, listOf(groupMembership, invalidMember))
            }
            assertEquals(before, queries.listGroups())
        }
}
