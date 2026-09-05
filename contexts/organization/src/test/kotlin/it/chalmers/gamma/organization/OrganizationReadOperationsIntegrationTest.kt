package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrganizationReadOperationsIntegrationTest {
    @Test
    fun `super group details and editor keep parent and related rows in one snapshot`() {
        for (editor in listOf(false, true)) {
            PostgresTestEnvironment().use { postgres ->
                var beforeRelatedRows: (() -> Unit)? = null
                var fired = false
                val observer =
                    object : StatementInterceptor {
                        override fun beforeExecution(
                            transaction: Transaction,
                            context: StatementContext,
                        ) {
                            val table = if (editor) "g_super_group_type" else "g_group"
                            if (context.statement.targets.none { it.tableName == table }) return
                            val mutation = beforeRelatedRows ?: return
                            beforeRelatedRows = null
                            try {
                                mutation()
                            } catch (failure: java.sql.SQLException) {
                                throw AssertionError("Concurrent fixture mutation failed", failure)
                            }
                            fired = true
                        }
                    }
                DatabaseFactory(postgres.dataSource, listOf(observer)).use { database ->
                    val queries = OrganizationQueries(database)
                    val read: () -> Any? = {
                        if (editor) queries.superGroupEditor(superGroupId) else queries.superGroupDetails(superGroupId)
                    }
                    val before = assertNotNull(read())
                    beforeRelatedRows = {
                        database.executeSqlScript(
                            """
                            UPDATE g_super_group SET pretty_name = 'Changed parent' WHERE super_group_id = '${superGroupId.value}';
                            UPDATE g_group SET pretty_name = 'Changed child' WHERE super_group_id = '${superGroupId.value}';
                            INSERT INTO g_super_group_type (super_group_type_name, created_at) VALUES ('snapshot', NOW());
                            """.trimIndent(),
                        )
                    }
                    assertEquals(before, read())
                    assertTrue(fired)
                    assertNotEquals(before, read())
                }
            }
        }
    }

    @Test
    fun `organization reads preserve projections and missing outcomes and reject ambient transactions`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val queries = OrganizationQueries(database)
                val details = assertNotNull(queries.superGroupDetails(superGroupId))
                assertEquals("digIT", details.superGroup.prettyName.value)
                assertEquals(listOf("digit2026"), details.groups.map { it.name.value })
                assertTrue(details.groups.all { it.superGroup == details.superGroup })
                val editor = assertNotNull(queries.superGroupEditor(superGroupId))
                assertEquals(details.superGroup, editor.superGroup)
                assertEquals(queries.listSuperGroupTypes(), editor.superGroupTypes)
                assertEquals(editor.superGroupTypes.sortedBy { it.value }, editor.superGroupTypes)
                assertTrue(editor.superGroup.type in editor.superGroupTypes)
                assertNull(queries.superGroupDetails(SuperGroupId.generate()))
                assertNull(queries.superGroupEditor(SuperGroupId.generate()))
                assertNull(queries.findPost(PostId.generate()))
                val post = queries.listPosts().first()
                assertEquals(post, queries.findPost(post.id))
                database.commitTransaction {
                    assertFailsWith<IllegalStateException> { queries.superGroupDetails(superGroupId) }
                    assertFailsWith<IllegalStateException> { queries.superGroupEditor(superGroupId) }
                    assertFailsWith<IllegalStateException> { queries.listSuperGroupTypes() }
                    assertFailsWith<IllegalStateException> { queries.findPost(post.id) }
                    assertFailsWith<IllegalStateException> { queries.findPost(PostId.generate()) }
                }
            }
        }
    }

    private companion object {
        val superGroupId = SuperGroupId.parse("aed27030-ad90-4526-855c-1e909b1dcecb")
    }
}
