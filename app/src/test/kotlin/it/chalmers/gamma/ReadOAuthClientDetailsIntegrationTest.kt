package it.chalmers.gamma

import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientDetails
import it.chalmers.gamma.oauth.OAuthClientNotFound
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.ReadOAuthClientDetails
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ReadOAuthClientDetailsIntegrationTest {
    @Test
    fun `client management rejects a stale administrator flag`() =
        withDatabase { database, _ ->
            val uid = createClient(database)
            assertFailsWith<AccessDenied> { read(database, owner.copy(isAdministrator = true), uid) }
        }

    @Test
    fun `client metadata and authorities describe one committed state`() =
        withDatabase { database, mutation ->
            val uid = createClient(database)
            val before = read(database, administrator, uid)
            mutation.beforeAuthorities = {
                database.executeSqlScript(
                    """
                    UPDATE g_client SET pretty_name = 'Changed' WHERE client_uid = '${uid.value}';
                    DELETE FROM g_client_authority_user WHERE client_uid = '${uid.value}';
                    DELETE FROM g_client_authority WHERE client_uid = '${uid.value}';
                    """.trimIndent(),
                )
            }
            assertEquals(before, read(database, administrator, uid))
            assertTrue(mutation.fired)
            assertNotEquals(before, read(database, administrator, uid))
        }

    @Test
    fun `details enforce current owner and account access and explicit transaction ownership`() =
        withDatabase { database, mutation ->
            val uid = createClient(database)
            assertEquals(uid, read(database, administrator.copy(isAdministrator = false), uid).client.uid)
            assertFailsWith<AccessDenied> { read(database, owner, uid) }
            database.executeSqlScript(
                "UPDATE g_client SET official = FALSE, created_by = '${ownerId.value}' WHERE client_uid = '${uid.value}'",
            )
            assertEquals(uid, read(database, owner, uid).client.uid)
            assertEquals("OAuthClientDetails(<redacted>)", read(database, owner, uid).toString())
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'")
            assertFailsWith<AccessDenied> { read(database, administrator, uid) }
            database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${ownerId.value}'")
            for (actor in listOf(owner, Actor.Anonymous, Actor.User(ActorUserId(UUID.randomUUID()), true))) {
                assertFailsWith<AccessDenied> { read(database, actor, uid) }
            }
            database.executeSqlScript("UPDATE g_user SET locked = FALSE WHERE user_id = '${ownerId.value}'")
            assertFailsWith<OAuthClientNotFound> { read(database, owner, ClientUid(UUID.randomUUID())) }
            database.executeSqlScript("UPDATE g_client SET client_id = NULL WHERE client_uid = '${uid.value}'")
            assertFailsWith<OAuthClientNotFound> { read(database, owner, uid) }
            lateinit var completed: JdbcTransaction
            database.commitTransaction {
                completed = this
                assertFailsWith<IllegalStateException> { read(database, owner, uid) }
            }
            val clients = OAuthClientQueries(database)
            assertFailsWith<IllegalStateException> { clients.findClientIn(completed, uid) }
            DatabaseFactory(mutation.source).use { foreign ->
                foreign.commitTransaction {
                    assertFailsWith<IllegalStateException> { clients.findClientIn(this, uid) }
                }
            }
        }

    private fun read(
        database: DatabaseFactory,
        actor: Actor,
        uid: ClientUid,
    ): OAuthClientDetails =
        ReadOAuthClientDetails(database, UserAccountAccess(database), OAuthClientQueries(database)).read(actor, uid)

    private fun createClient(database: DatabaseFactory): ClientUid {
        val creation = CreateClient(database, bcryptCost = 10)
        val prepared =
            creation.prepare(
                NewOAuthClient(
                    RedirectUri("https://example.org/callback"),
                    ClientName("Management snapshot"),
                    LocalizedText.of(),
                    false,
                    ClientOwner.Official,
                ),
            )
        return database.commitTransaction {
            val uid = creation.insertIn(this, prepared).client.uid
            val authorities = ClientAuthorities(database)
            authorities.createIn(
                this,
                authorities.lockIn(this, uid),
                AuthorityName("manage"),
                setOf(ownerId),
                emptySet(),
            )
            uid
        }
    }

    private fun withDatabase(test: (DatabaseFactory, AuthorityReadMutation) -> Unit) {
        PostgresTestEnvironment().use { postgres ->
            val mutation = AuthorityReadMutation(postgres.dataSource)
            DatabaseFactory(postgres.dataSource, listOf(mutation)).use { database -> test(database, mutation) }
        }
    }

    private companion object {
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val owner = Actor.User(ActorUserId(ownerId.value))
        val administrator = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
    }
}

private class AuthorityReadMutation(
    val source: javax.sql.DataSource,
) : StatementInterceptor {
    var beforeAuthorities: (() -> Unit)? = null
    var fired = false

    override fun beforeExecution(
        transaction: Transaction,
        context: StatementContext,
    ) {
        if (context.statement.targets.any { it.tableName == "g_client_authority_user" }) {
            val mutation = beforeAuthorities ?: return
            beforeAuthorities = null
            fired = true
            mutation()
        }
    }
}
