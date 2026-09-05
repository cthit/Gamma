package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateSuperGroupIntegrationTest {
    @Test
    fun `updates metadata and translations and rejects a stale edit`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.superGroupDetails(existingSuperGroupId)?.superGroup)
            val input = edit(original)
            val operation = UpdateSuperGroup(database)
            operation.update(groupAdministrator, input)
            val saved = assertNotNull(queries.superGroupDetails(original.id)?.superGroup)
            assertEquals(input.name, saved.name)
            assertEquals(input.prettyName, saved.prettyName)
            assertEquals(input.description, saved.description)
            assertEquals(original.version + 1, saved.version)
            assertFailsWith<OrganizationConflict> { operation.update(groupAdministrator, input) }
            assertEquals(saved, queries.superGroupDetails(original.id)?.superGroup)
        }

    @Test
    fun `description persistence failure rolls back metadata and version`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.superGroupDetails(existingSuperGroupId)?.superGroup)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_description_update() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    RAISE EXCEPTION 'forced description persistence failure';
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_description_update BEFORE UPDATE ON g_text
                    FOR EACH ROW EXECUTE FUNCTION reject_description_update();
                """.trimIndent(),
            )
            assertFails { UpdateSuperGroup(database).update(groupAdministrator, edit(original)) }
            assertEquals(original, queries.superGroupDetails(original.id)?.superGroup)
        }

    @Test
    fun `denied edit leaves metadata and translations unchanged`() =
        withGroupDatabase { database, queries ->
            val original = assertNotNull(queries.superGroupDetails(existingSuperGroupId)?.superGroup)
            assertFailsWith<AccessDenied> { UpdateSuperGroup(database).update(ordinaryGroupUser, edit(original)) }
            assertEquals(original, queries.superGroupDetails(original.id)?.superGroup)
        }

    private fun edit(group: SuperGroup) =
        SuperGroupUpdate(
            group.id,
            group.version,
            OrganizationName("edited-super-group"),
            PrettyName("Edited super group"),
            group.type,
            LocalizedText.of("Redigerad", "Edited description"),
        )
}
