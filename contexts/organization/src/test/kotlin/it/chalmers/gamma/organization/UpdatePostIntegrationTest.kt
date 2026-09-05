package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdatePostIntegrationTest {
    @Test
    fun `editing a legacy post treats null version order and prefix as defaults`() =
        withGroupDatabase { database, queries ->
            val id = groupMembership.postId
            database.executeSqlScript(
                "UPDATE g_post SET version = NULL, post_order = NULL, email_prefix = NULL WHERE post_id = '${id.value}'",
            )
            val original = assertNotNull(queries.findPost(id))
            assertEquals(0, original.version)
            assertEquals(0, original.order.value)
            assertEquals("", original.emailPrefix.value)
            val input = PostUpdate(id, original.version, original.name, EmailPrefix("restored"))
            UpdatePost(database, organizationAccess(database)).update(groupAdministrator, input)
            val saved = assertNotNull(queries.findPost(id))
            assertEquals(1, saved.version)
            assertEquals(input.emailPrefix, saved.emailPrefix)
        }

    @Test
    fun `editing names preserves a newer order and rejects stale metadata`() =
        withGroupDatabase { database, queries ->
            val original = queries.listPosts().first()
            val input =
                PostUpdate(
                    original.id,
                    original.version,
                    LocalizedText.of("Redigerad", "Edited"),
                    EmailPrefix("edited"),
                )
            val newOrder = queries.listPosts().map { it.id }.reversed()
            ReorderPosts(database, organizationAccess(database)).reorder(groupAdministrator, newOrder)
            val operation = UpdatePost(database, organizationAccess(database))
            operation.update(groupAdministrator, input)
            val saved = assertNotNull(queries.findPost(original.id))
            assertEquals(input.name, saved.name)
            assertEquals(input.emailPrefix, saved.emailPrefix)
            assertEquals(original.version + 1, saved.version)
            assertEquals(newOrder, queries.listPosts().map { it.id })
            assertFailsWith<OrganizationConflict> { operation.update(groupAdministrator, input) }
            assertEquals(saved, queries.findPost(original.id))
        }

    @Test
    fun `name persistence failure rolls back prefix and version`() =
        withGroupDatabase { database, queries ->
            val original = queries.listPosts().first()
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_post_name() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    RAISE EXCEPTION 'forced name persistence failure';
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_post_name BEFORE UPDATE ON g_text
                    FOR EACH ROW EXECUTE FUNCTION reject_post_name();
                """.trimIndent(),
            )
            assertFails {
                UpdatePost(database, organizationAccess(database)).update(
                    groupAdministrator,
                    PostUpdate(
                        original.id,
                        original.version,
                        LocalizedText.of("Redigerad", "Edited"),
                        EmailPrefix("edited"),
                    ),
                )
            }
            assertEquals(original, queries.findPost(original.id))
        }

    @Test
    fun `denied and missing post updates leave existing data unchanged`() =
        withGroupDatabase { database, queries ->
            val before = queries.listPosts()
            val original = before.first()
            val input = PostUpdate(original.id, original.version, original.name, EmailPrefix("denied"))
            assertFailsWith<AccessDenied> {
                UpdatePost(
                    database,
                    organizationAccess(database),
                ).update(ordinaryGroupUser, input)
            }
            assertFailsWith<OrganizationConflict> {
                UpdatePost(
                    database,
                    organizationAccess(database),
                ).update(groupAdministrator, input.copy(postId = PostId.generate()))
            }
            assertEquals(before, queries.listPosts())
        }
}
