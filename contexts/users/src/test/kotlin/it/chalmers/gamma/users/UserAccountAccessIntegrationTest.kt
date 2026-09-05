package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserAccountAccessIntegrationTest {
    @Test
    fun `account access uses current authority and rejects locked missing or anonymous accounts`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val access = UserAccountAccess(database)
                database.commitTransaction {
                    val administrator = access.requireIn(this, Actor.User(ActorUserId(administratorId.value)))
                    assertEquals(administratorId, administrator.userId)
                    assertTrue(administrator.isAdministrator)
                    val member = access.requireIn(this, Actor.User(ActorUserId(memberId.value), true))
                    assertFalse(member.isAdministrator)
                    assertFailsWith<AccessDenied> { access.requireIn(this, Actor.Anonymous) }
                }
                database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${memberId.value}'")
                database.commitTransaction {
                    assertFailsWith<AccessDenied> { access.requireIn(this, Actor.User(ActorUserId(memberId.value))) }
                }
                database.executeSqlScript("DELETE FROM g_user WHERE user_id = '${memberId.value}'")
                database.commitTransaction {
                    assertFailsWith<AccessDenied> { access.requireIn(this, Actor.User(ActorUserId(memberId.value))) }
                }
            }
        }
    }

    @Test
    fun `account access rejects foreign and completed transaction handles`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val access = UserAccountAccess(database)
                    val actor = Actor.User(ActorUserId(administratorId.value))
                    other.commitTransaction { assertFailsWith<IllegalStateException> { access.requireIn(this, actor) } }
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> { access.requireIn(completed, actor) }
                }
            }
        }
    }

    private companion object {
        val administratorId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val memberId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
    }
}
