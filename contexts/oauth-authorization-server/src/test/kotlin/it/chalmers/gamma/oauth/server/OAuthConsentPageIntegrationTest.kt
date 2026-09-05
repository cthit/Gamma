package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.springframework.security.web.csrf.DefaultCsrfToken
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OAuthConsentPageIntegrationTest {
    @Test
    fun `a client deleted during consent lookup retains its unofficial owner warning`() {
        PostgresTestEnvironment().use { postgres ->
            var beforeThirdRead: (() -> Unit)? = null
            var reads = 0
            val interceptor =
                object : StatementInterceptor {
                    override fun beforeExecution(
                        transaction: Transaction,
                        context: StatementContext,
                    ) {
                        if (beforeThirdRead != null && ++reads == 3) {
                            val mutation = beforeThirdRead
                            beforeThirdRead = null
                            mutation?.invoke()
                        }
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val creation = CreateClient(database, bcryptCost = 10)
                val prepared =
                    creation.prepare(
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("Unofficial application"),
                            LocalizedText.of(),
                            false,
                            ClientOwner.User(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")),
                        ),
                    )
                val client = database.commitTransaction { creation.insertIn(this, prepared).client }
                val clients = OAuthProtocolClients(database)
                val controller = ConsentController(ReadOAuthConsent(database, clients, UserQueries(database)))
                beforeThirdRead = {
                    database.executeSqlScript(
                        """
                        DELETE FROM g_client_scope WHERE client_uid = '${client.uid.value}';
                        DELETE FROM g_client WHERE client_uid = '${client.uid.value}';
                        """.trimIndent(),
                    )
                }
                val html =
                    controller.consent(
                        client.clientId.value,
                        "openid profile",
                        "request-state",
                        DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "csrf-value"),
                    )
                assertTrue(
                    reads >= 3,
                    "Expected consent reads, observed $reads; page: ${Regex(
                        "<title>(.*?)</title>",
                    ).find(html)?.groupValues?.get(1)}",
                )
                assertNull(clients.serverClient(client.uid))
                assertTrue(html.contains("not an approved"))
                assertTrue(html.contains("Michael"))
            }
        }
    }
}
