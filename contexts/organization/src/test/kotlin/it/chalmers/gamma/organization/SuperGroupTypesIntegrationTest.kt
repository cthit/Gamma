package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuperGroupTypesIntegrationTest {
    @Test
    fun `creates and deletes unused types with explicit duplicate and missing outcomes`() =
        withGroupDatabase { database, queries ->
            val operation = SuperGroupTypes(database)
            val type = SuperGroupType("testtype")
            operation.create(groupAdministrator, type)
            assertTrue(type in queries.listSuperGroupTypes())
            assertFailsWith<OrganizationConflict> { operation.create(groupAdministrator, type) }
            operation.delete(groupAdministrator, type)
            assertFalse(type in queries.listSuperGroupTypes())
            assertFailsWith<OrganizationNotFound> { operation.delete(groupAdministrator, type) }
        }

    @Test
    fun `denies nonadministrators and refuses to delete types used by super groups`() =
        withGroupDatabase { database, queries ->
            val before = queries.listSuperGroupTypes()
            val operation = SuperGroupTypes(database)
            val used = queries.listSuperGroups().first().type
            assertFailsWith<AccessDenied> { operation.create(ordinaryGroupUser, SuperGroupType("deniedtype")) }
            assertFailsWith<AccessDenied> { operation.delete(ordinaryGroupUser, used) }
            assertFailsWith<OrganizationConflict> { operation.delete(groupAdministrator, used) }
            assertEquals(before, queries.listSuperGroupTypes())
        }
}
