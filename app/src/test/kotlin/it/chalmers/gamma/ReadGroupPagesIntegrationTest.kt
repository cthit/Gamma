package it.chalmers.gamma

import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.ReadGroupPages
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.DirectoryUserPageRequest
import it.chalmers.gamma.users.DirectoryUserScope
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadGroupPagesIntegrationTest {
    @Test
    fun `group pages retain one snapshot across memberships directory and post reads`() {
        for (projection in 0..2) {
            PostgresTestEnvironment().use { postgres ->
                var beforeRead: (() -> Unit)? = null
                var fired = false
                var userReads = 0
                val observer =
                    object : StatementInterceptor {
                        override fun beforeExecution(
                            transaction: Transaction,
                            context: StatementContext,
                        ) {
                            if (context.statement.targets.any { it.tableName == "g_user" }) userReads++
                            val target = if (projection == 2) "g_post" else "g_membership"
                            if (context.statement.targets.none { it.tableName == target }) return
                            val mutation = beforeRead ?: return
                            beforeRead = null
                            try {
                                mutation()
                            } catch (failure: java.sql.SQLException) {
                                throw AssertionError("Concurrent fixture mutation failed", failure)
                            }
                            fired = true
                        }
                    }
                DatabaseFactory(postgres.dataSource, listOf(observer)).use { database ->
                    val reads = reads(database)
                    val read: () -> Any? = {
                        when (projection) {
                            0 -> reads.details(administrator, groupId)
                            1 -> reads.editor(administrator, groupId)
                            else -> reads.newMember(administrator)
                        }
                    }
                    val before = assertNotNull(read())
                    userReads = 0
                    beforeRead = {
                        database.executeSqlScript(
                            """
                            UPDATE g_group SET pretty_name = 'Changed group' WHERE group_id = '${groupId.value}';
                            UPDATE g_membership SET unofficial_post_name = 'Changed post name' WHERE group_id = '${groupId.value}';
                            UPDATE g_user SET nick = 'Changed member' WHERE user_id = '${memberId.value}';
                            UPDATE g_post SET email_prefix = 'changed';
                            """.trimIndent(),
                        )
                    }
                    assertEquals(before, read())
                    assertTrue(fired)
                    assertEquals(2, userReads, "One account check plus one batched directory query")
                    assertNotEquals(before, read())
                }
            }
        }
    }

    @Test
    fun `group pages use current administrator authority and reject missing or locked accounts`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val reads = reads(database)
                val current = administrator.copy(isAdministrator = false)
                assertEquals(3, assertNotNull(reads.details(current, groupId)).memberships.size)
                assertEquals(3, assertNotNull(reads.editor(current, groupId)).memberships.size)
                assertTrue(reads.newMember(current).users.isNotEmpty())
                assertNull(reads.details(current, GroupId.generate()))
                assertNull(reads.editor(current, GroupId.generate()))
                val missing = Actor.User(ActorUserId(UserId.generate().value), true)
                for (actor in listOf(Actor.Anonymous, missing, Actor.User(ActorUserId(memberId.value), true))) {
                    assertFailsWith<AccessDenied> { reads.details(actor, groupId) }
                    assertFailsWith<AccessDenied> { reads.editor(actor, groupId) }
                    assertFailsWith<AccessDenied> { reads.newMember(actor) }
                }
                database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${adminId.value}'")
                assertFailsWith<AccessDenied> { reads.details(administrator, groupId) }
                assertFailsWith<AccessDenied> { reads.editor(administrator, groupId) }
                assertFailsWith<AccessDenied> { reads.newMember(administrator) }
                database.executeSqlScript(
                    "UPDATE g_user SET locked = FALSE WHERE user_id = '${adminId.value}'; DELETE FROM g_admin_user",
                )
                assertFailsWith<AccessDenied> { reads.details(administrator, groupId) }
                assertFailsWith<AccessDenied> { reads.editor(administrator, groupId) }
                assertFailsWith<AccessDenied> { reads.newMember(administrator) }
            }
        }
    }

    @Test
    fun `page owners reject ambient transactions and query participants reject foreign or completed handles`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val reads = reads(database)
                val organizations = OrganizationQueries(database)
                val users = UserQueries(database)
                val directory = DirectoryUserPageRequest("", null, DirectoryUserScope.administrator(adminId))
                lateinit var completed: JdbcTransaction
                database.commitTransaction {
                    completed = this
                    assertFailsWith<IllegalStateException> { reads.details(administrator, groupId) }
                    assertFailsWith<IllegalStateException> { reads.editor(administrator, groupId) }
                    assertFailsWith<IllegalStateException> { reads.newMember(administrator) }
                    assertFailsWith<IllegalStateException> { organizations.findGroup(groupId) }
                    assertNotNull(organizations.findGroupIn(this, groupId))
                    assertEquals(3, organizations.membershipsForGroupIn(this, groupId).size)
                    assertEquals(emptyList(), users.directoryUsersByIdsIn(this, emptySet()))
                }
                val participants =
                    listOf<(JdbcTransaction) -> Any?>(
                        { organizations.findGroupIn(it, groupId) },
                        { organizations.membershipsForGroupIn(it, groupId) },
                        { users.directoryUserPageIn(it, directory) },
                        { users.directoryUsersByIdsIn(it, emptySet()) },
                        { users.directoryUsersByIdsIn(it, setOf(memberId)) },
                    )
                for (read in participants) assertFailsWith<IllegalStateException> { read(completed) }
                DatabaseFactory(postgres.dataSource).use { foreign ->
                    foreign.commitTransaction {
                        for (read in participants) assertFailsWith<IllegalStateException> { read(this) }
                    }
                }
            }
        }
    }

    @Test
    fun `new member options and editor include an unassigned user beyond the first directory page`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                database.executeSqlScript(
                    """
                    INSERT INTO g_user (user_id, cid, nick, first_name, last_name, email, acceptance_year, version, created_at, updated_at)
                    SELECT gen_random_uuid(), 'candidate' || chr(97 + n / 26) || chr(97 + n % 26), 'Candidate ' || n,
                           'Candidate', 'Member', 'candidate' || n || '@example.org', 2020, 0, NOW(), NOW()
                    FROM generate_series(1, 205) AS n;
                    UPDATE g_user SET cid = 'zzcandidate' WHERE user_id = '${memberId.value}';
                    DELETE FROM g_membership WHERE user_id = '${memberId.value}';
                    """.trimIndent(),
                )
                val reads = reads(database)
                val editor = assertNotNull(reads.editor(administrator, groupId))
                assertTrue(editor.memberships.none { it.userId == memberId })
                assertTrue(editor.users.indexOfFirst { it.id == memberId } >= 200)
                assertTrue(reads.newMember(administrator).users.indexOfFirst { it.id == memberId } >= 200)
            }
        }
    }

    private fun reads(database: DatabaseFactory) =
        ReadGroupPages(database, UserAccountAccess(database), OrganizationQueries(database), UserQueries(database))

    private companion object {
        val groupId = GroupId.parse("047ac437-a789-4cc5-bb6e-ba50efd7c509")
        val memberId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val administrator = Actor.User(ActorUserId(adminId.value), true)
    }
}
