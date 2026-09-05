package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeleteGroupIntegrationTest {
    @Test
    fun `deletes group memberships and image pointers while preserving parent and posts`() =
        withGroupDatabase { database, queries ->
            val group = assertNotNull(queries.findGroup(existingGroupId))
            val posts = queries.listPosts()
            val parent = queries.superGroupDetails(group.superGroup.id)?.superGroup
            assertTrue(
                database
                    .commitTransaction(
                        readOnly = true,
                    ) { queries.membershipsForGroupIn(this, group.id) }
                    .isNotEmpty(),
            )
            database.executeSqlScript(
                """
                INSERT INTO g_group_images_uri (created_at, updated_at, group_id, avatar_uri, version)
                VALUES (NOW(), NOW(), '${group.id.value}', 'deleted-group.png', 0)
                """.trimIndent(),
            )

            DeleteGroup(database).delete(groupAdministrator, group.id)

            assertNull(queries.findGroup(group.id))
            assertEquals(
                emptyList(),
                database.commitTransaction(readOnly = true) { queries.membershipsForGroupIn(this, group.id) },
            )
            val images =
                database.commitTransaction(readOnly = true) {
                    GroupImagesTable.selectAll().where { GroupImagesTable.groupId eq group.id.value }.count()
                }
            assertEquals(0L, images)
            assertEquals(posts, queries.listPosts())
            assertEquals(parent, queries.superGroupDetails(group.superGroup.id)?.superGroup)
        }

    @Test
    fun `denied and missing group deletions do not mutate existing state`() =
        withGroupDatabase { database, queries ->
            val groups = queries.listGroups()
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, existingGroupId) }
            val operation = DeleteGroup(database)

            assertFailsWith<AccessDenied> { operation.delete(ordinaryGroupUser, existingGroupId) }
            assertFailsWith<OrganizationNotFound> { operation.delete(groupAdministrator, GroupId.generate()) }

            assertEquals(groups, queries.listGroups())
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, existingGroupId)
                },
            )
        }
}
