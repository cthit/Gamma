package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
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

class UserReadOperationsIntegrationTest {
    @Test
    fun `administrative profile and GDPR training describe one committed state`() {
        PostgresTestEnvironment().use { postgres ->
            var beforeProfile: (() -> Unit)? = null
            var fired = false
            val observer =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (context.statement.targets.any { it.tableName == "g_user" }) {
                            val mutation = beforeProfile ?: return
                            beforeProfile = null
                            try {
                                mutation()
                            } catch (failure: java.sql.SQLException) {
                                throw AssertionError("Concurrent fixture mutation failed", failure)
                            }
                            fired = true
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(observer)).use { database ->
                val queries = UserQueries(database)
                val before = queries.administrativeUser(adminId, ownerId)
                beforeProfile = {
                    database.executeSqlScript(
                        """
                        UPDATE g_user SET nick = 'Changed' WHERE user_id = '${ownerId.value}';
                        INSERT INTO g_gdpr_trained (user_id, created_at) VALUES ('${ownerId.value}', CURRENT_TIMESTAMP);
                        """.trimIndent(),
                    )
                }
                assertEquals(before, queries.administrativeUser(adminId, ownerId))
                assertTrue(fired)
                assertNotEquals(before, queries.administrativeUser(adminId, ownerId))
            }
        }
    }

    @Test
    fun `personal profile owns the authenticated identity and preserves missing account behavior`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val owner = Actor.User(ActorUserId(ownerId.value))
            val profile = queries.myProfile(owner)
            assertEquals(queries.findUser(ownerId), profile)
            assertEquals(ownerId, profile.id)
            assertFailsWith<AccessDenied> { queries.myProfile(Actor.Anonymous) }
            assertFailsWith<UserNotFound> { queries.myProfile(Actor.User(ActorUserId(UserId.generate().value))) }
            database.executeSqlScript("UPDATE g_user SET nick = 'Changed' WHERE user_id = '${ownerId.value}'")
            assertEquals("Changed", queries.myProfile(owner).nick.value)
            assertEquals("UserProfile(<redacted>)", profile.toString())
        }

    @Test
    fun `complete user reads reject enclosing transactions and administrators are checked from the database`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val request = DirectoryUserPageRequest("", null, DirectoryUserScope.administrator(adminId))
            val reads =
                listOf<() -> Any?>(
                    { queries.findUser(ownerId) },
                    { queries.myProfile(Actor.User(ActorUserId(ownerId.value))) },
                    { queries.administrativeUser(adminId, ownerId) },
                    { queries.administrativeUsers(adminId) },
                    { queries.directoryUserPage(request) },
                    { queries.directoryUserPage(request.copy(scope = DirectoryUserScope.visibleToUser(ownerId))) },
                )
            database.commitTransaction {
                for (read in reads) assertFailsWith<IllegalStateException> { read() }
            }
            assertNotNull(queries.administrativeUser(adminId, ownerId))
            assertNull(queries.administrativeUser(adminId, UserId.generate()))
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${adminId.value}'")
            assertFailsWith<AccessDenied> { queries.administrativeUser(adminId, ownerId) }
            assertFailsWith<AccessDenied> { queries.administrativeUsers(adminId) }
            assertFailsWith<AccessDenied> { queries.directoryUserPage(request) }
            assertEquals(ownerId, queries.myProfile(Actor.User(ActorUserId(ownerId.value))).id)
        }

    private companion object {
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
    }
}
