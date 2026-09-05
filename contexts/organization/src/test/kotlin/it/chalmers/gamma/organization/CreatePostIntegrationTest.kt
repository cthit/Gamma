package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreatePostIntegrationTest {
    private val input = NewPost(LocalizedText.of("Testare", "Tester"), EmailPrefix("tester"))

    @Test
    fun `creates names prefix and order as one committed post`() =
        withGroupDatabase { database, queries ->
            val expectedOrder = queries.listPosts().maxOf { it.order.value } + 1
            val id = CreatePost(database, organizationAccess(database)).create(groupAdministrator, input)
            val saved = assertNotNull(queries.findPost(id))
            assertEquals(input.name, saved.name)
            assertEquals(input.emailPrefix, saved.emailPrefix)
            assertEquals(expectedOrder, saved.order.value)
            assertEquals(0, saved.version)
        }

    @Test
    fun `authorizes before checking required post names and persists no rejected data`() =
        withGroupDatabase { database, queries ->
            val before = queries.listPosts()
            val invalid = input.copy(name = LocalizedText.of())
            val operation = CreatePost(database, organizationAccess(database))
            assertFailsWith<AccessDenied> { operation.create(ordinaryGroupUser, invalid) }
            assertFailsWith<IllegalArgumentException> { operation.create(groupAdministrator, invalid) }
            assertEquals(before, queries.listPosts())
        }

    @Test
    fun `post insert failure rolls back the preceding translated name`() =
        withGroupDatabase { database, queries ->
            val before = queries.listPosts()
            val names = database.commitTransaction(readOnly = true) { SharedLocalizedTextsTable.selectAll().count() }
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_post_insert() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    RAISE EXCEPTION 'forced post persistence failure';
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_post_insert BEFORE INSERT ON g_post
                    FOR EACH ROW EXECUTE FUNCTION reject_post_insert();
                """.trimIndent(),
            )
            assertFails { CreatePost(database, organizationAccess(database)).create(groupAdministrator, input) }
            assertEquals(before, queries.listPosts())
            assertEquals(
                names,
                database.commitTransaction(readOnly = true) { SharedLocalizedTextsTable.selectAll().count() },
            )
        }

    @Test
    fun `creation appends after the last position when an earlier post was deleted`() =
        withGroupDatabase { database, queries ->
            val operation = CreatePost(database, organizationAccess(database))
            val first = operation.create(groupAdministrator, input)
            operation.create(groupAdministrator, input.copy(emailPrefix = EmailPrefix("second")))
            val last = operation.create(groupAdministrator, input.copy(emailPrefix = EmailPrefix("third")))
            val lastOrder = assertNotNull(queries.findPost(last)).order.value
            DeletePost(database, organizationAccess(database)).delete(groupAdministrator, first)
            val appended = operation.create(groupAdministrator, input.copy(emailPrefix = EmailPrefix("fourth")))
            assertEquals(lastOrder + 1, assertNotNull(queries.findPost(appended)).order.value)
            val orders = queries.listPosts().map { it.order.value }
            assertEquals(orders.size, orders.toSet().size)
        }

    @Test
    fun `concurrent creations reserve different final positions`() =
        withGroupDatabase { database, queries ->
            val nextOrder = queries.listPosts().maxOf { it.order.value } + 1
            val ready = CountDownLatch(2)
            val ids =
                Executors.newFixedThreadPool(2).use { workers ->
                    val creations =
                        (1..2).map {
                            workers.submit<PostId> {
                                ready.countDown()
                                check(ready.await(10, TimeUnit.SECONDS))
                                CreatePost(database, organizationAccess(database)).create(groupAdministrator, input)
                            }
                        }
                    creations.map { it.get(10, TimeUnit.SECONDS) }
                }
            assertEquals(
                setOf(nextOrder, nextOrder + 1),
                ids.map { assertNotNull(queries.findPost(it)).order.value }.toSet(),
            )
        }
}
