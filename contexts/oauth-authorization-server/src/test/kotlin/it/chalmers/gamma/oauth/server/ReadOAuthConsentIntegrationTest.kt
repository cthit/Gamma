package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientId
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.oauth.Scope
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
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

class ReadOAuthConsentIntegrationTest {
    @Test
    fun `client metadata scopes and owner details share one snapshot`() {
        PostgresTestEnvironment().use { postgres ->
            var beforeSecondRead: (() -> Unit)? = null
            var reads = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (beforeSecondRead != null && ++reads == 2) {
                            val mutation = beforeSecondRead
                            beforeSecondRead = null
                            mutation?.invoke()
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val creation = CreateClient(database, bcryptCost = 10)
                val prepared = creation.prepare(input)
                val client = database.commitTransaction { creation.insertIn(this, prepared).client }
                database.executeSqlScript("UPDATE g_client_scope SET scope = LOWER(scope)")
                val read = ReadOAuthConsent(database, OAuthProtocolClients(database), UserQueries(database))
                val before = assertNotNull(read.read(client.clientId))
                assertEquals(client.scopes, before.client.scopes)
                assertEquals(userId, before.owner?.id)
                assertEquals("Boss", before.owner?.nick?.value)
                beforeSecondRead = {
                    database.executeSqlScript(
                        """
                        UPDATE g_client SET pretty_name = 'Changed', created_by = 'bc605869-9a4d-46ec-8a29-d00819d4c195'
                            WHERE client_uid = '${client.uid.value}';
                        UPDATE g_user SET nick = 'Changed' WHERE user_id = '${userId.value}';
                        DELETE FROM g_client_scope WHERE client_uid = '${client.uid.value}';
                        """.trimIndent(),
                    )
                }
                assertEquals(before, read.read(client.clientId))
                val after = assertNotNull(read.read(client.clientId))
                assertNotEquals(before, after)
                assertEquals(setOf(Scope.OPENID), after.client.scopes)
                assertEquals("Jim", after.owner?.firstName?.value)
                assertTrue("Boss" !in before.toString())
                assertNull(read.read(ClientId("Z".repeat(30))))
            }
        }
    }

    @Test
    fun `consent rejects ambient reads and its queries reject foreign or completed transactions`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val creation = CreateClient(database, bcryptCost = 10)
                    val prepared = creation.prepare(input.copy(owner = ClientOwner.Official))
                    val client = database.commitTransaction { creation.insertIn(this, prepared).client }
                    val clients = OAuthProtocolClients(database)
                    val users = UserQueries(database)
                    val groups = OrganizationQueries(database)
                    val read = ReadOAuthConsent(database, clients, users)
                    assertNull(assertNotNull(read.read(client.clientId)).owner)
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction {
                        completed = this
                        assertFailsWith<IllegalStateException> { read.read(client.clientId) }
                    }
                    val participants =
                        listOf<(JdbcTransaction) -> Any?>(
                            { clients.consentDetailsIn(it, client.clientId) },
                            { clients.restrictionsIn(it, client.uid) },
                            { users.findDirectoryUserIn(it, userId) },
                            { groups.isMemberOfAnySuperGroupIn(it, userId, emptySet()) },
                        )
                    for (participant in participants) assertFailsWith<IllegalStateException> { participant(completed) }
                    other.commitTransaction {
                        for (participant in participants) assertFailsWith<IllegalStateException> { participant(this) }
                    }
                }
            }
        }
    }

    private companion object {
        val userId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val input =
            NewOAuthClient(
                RedirectUri("https://example.org/callback"),
                ClientName("Consent client"),
                LocalizedText.of(),
                true,
                ClientOwner.User(userId),
            )
    }
}
