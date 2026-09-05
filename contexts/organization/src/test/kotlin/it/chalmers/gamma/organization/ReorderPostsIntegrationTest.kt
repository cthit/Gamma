package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ReorderPostsIntegrationTest {
    @Test
    fun `reorders the complete list and rejects denied partial duplicate and unknown lists`() =
        withGroupDatabase { database, queries ->
            val operation = ReorderPosts(database)
            val reversed = queries.listPosts().map { it.id }.reversed()
            operation.reorder(groupAdministrator, reversed)
            assertEquals(reversed, queries.listPosts().map { it.id })
            val committed = queries.listPosts()
            assertEquals(committed.indices.toList(), committed.map { it.order.value })
            assertFailsWith<AccessDenied> { operation.reorder(ordinaryGroupUser, emptyList()) }
            assertFailsWith<IllegalArgumentException> { operation.reorder(groupAdministrator, reversed.dropLast(1)) }
            assertFailsWith<IllegalArgumentException> {
                operation.reorder(
                    groupAdministrator,
                    reversed.dropLast(1) + reversed.first(),
                )
            }
            assertFailsWith<IllegalArgumentException> {
                operation.reorder(
                    groupAdministrator,
                    reversed.dropLast(1) + PostId.generate(),
                )
            }
            assertEquals(committed, queries.listPosts())
        }

    @Test
    fun `later persistence failure rolls back earlier order changes`() =
        withGroupDatabase { database, queries ->
            val before = queries.listPosts()
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_post_order() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF NEW.post_order = 1 THEN
                        RAISE EXCEPTION 'forced order persistence failure';
                    END IF;
                    RETURN NEW;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_post_order BEFORE UPDATE ON g_post
                    FOR EACH ROW EXECUTE FUNCTION reject_post_order();
                """.trimIndent(),
            )
            assertFails { ReorderPosts(database).reorder(groupAdministrator, before.map { it.id }.reversed()) }
            assertEquals(before, queries.listPosts())
        }

    @Test
    fun `competing reorders leave one complete submitted order`() =
        withGroupDatabase { database, queries ->
            val ids = queries.listPosts().map { it.id }
            val orders = listOf(ids.reversed(), ids.drop(1) + ids.first())
            val ready = CountDownLatch(2)
            Executors.newFixedThreadPool(2).use { workers ->
                val changes =
                    orders.map { order ->
                        workers.submit {
                            ready.countDown()
                            check(ready.await(10, TimeUnit.SECONDS))
                            ReorderPosts(database).reorder(groupAdministrator, order)
                        }
                    }
                changes.forEach { it.get(10, TimeUnit.SECONDS) }
            }
            val committed = queries.listPosts()
            assertTrue(committed.map { it.id } in orders)
            assertEquals(committed.indices.toList(), committed.map { it.order.value })
        }
}
