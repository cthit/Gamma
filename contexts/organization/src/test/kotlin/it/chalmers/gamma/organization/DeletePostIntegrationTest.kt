package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DeletePostIntegrationTest {
    @Test
    fun `deletes an unused post and its translated name`() =
        withGroupDatabase { database, queries ->
            val before = queries.listPosts()
            val names = database.commitTransaction(readOnly = true) { SharedLocalizedTextsTable.selectAll().count() }
            val id =
                CreatePost(database, organizationAccess(database)).create(
                    groupAdministrator,
                    NewPost(LocalizedText.of("Tillfällig", "Temporary"), EmailPrefix("temporary")),
                )
            DeletePost(database, organizationAccess(database)).delete(groupAdministrator, id)
            assertNull(queries.findPost(id))
            assertEquals(before, queries.listPosts())
            assertEquals(
                names,
                database.commitTransaction(readOnly = true) { SharedLocalizedTextsTable.selectAll().count() },
            )
        }

    @Test
    fun `denied missing and still used posts cannot remove names or memberships`() =
        withGroupDatabase { database, queries ->
            val before = queries.listPosts()
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { queries.membershipsForGroupIn(this, existingGroupId) }
            val used = memberships.first().postId
            val operation = DeletePost(database, organizationAccess(database))
            assertFailsWith<AccessDenied> { operation.delete(ordinaryGroupUser, used) }
            assertFailsWith<OrganizationNotFound> { operation.delete(groupAdministrator, PostId.generate()) }
            assertFailsWith<OrganizationConflict> { operation.delete(groupAdministrator, used) }
            assertEquals(before, queries.listPosts())
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, existingGroupId)
                },
            )
        }
}
