package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreateSuperGroupIntegrationTest {
    private val input =
        NewSuperGroup(
            OrganizationName("created-super-group"),
            PrettyName("Created super group"),
            SuperGroupType("committee"),
            LocalizedText.of("Beskrivning", "Description"),
        )

    @Test
    fun `creates metadata and both description translations together`() =
        withGroupDatabase { database, queries ->
            val id = CreateSuperGroup(database, organizationAccess(database)).create(groupAdministrator, input)
            val saved = assertNotNull(queries.superGroupDetails(id)?.superGroup)
            assertEquals(input.name, saved.name)
            assertEquals(input.prettyName, saved.prettyName)
            assertEquals(input.type, saved.type)
            assertEquals(input.description, saved.description)
            assertEquals(0, saved.version)
        }

    @Test
    fun `denied creation and invalid type leave no super group or orphan description`() =
        withGroupDatabase { database, queries ->
            val groups = queries.listSuperGroups()
            val textCount =
                database.commitTransaction(
                    readOnly = true,
                ) { SharedLocalizedTextsTable.selectAll().count() }
            val operation = CreateSuperGroup(database, organizationAccess(database))
            assertFailsWith<AccessDenied> { operation.create(ordinaryGroupUser, input) }
            assertFails { operation.create(groupAdministrator, input.copy(type = SuperGroupType("missingtype"))) }
            assertEquals(groups, queries.listSuperGroups())
            assertEquals(
                textCount,
                database.commitTransaction(readOnly = true) { SharedLocalizedTextsTable.selectAll().count() },
            )
        }
}
