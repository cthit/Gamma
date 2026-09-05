package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OAuthClientAccessIntegrationTest {
    @Test
    fun `a missing client is denied rather than treated as unrestricted`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                assertFalse(access(database).allowed(userId, ClientUid.generate()))
            }
        }
    }

    @Test
    fun `old account state and newly granted membership cannot combine into access`() {
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
                val prepared =
                    creation.prepare(
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("Restricted application"),
                            LocalizedText.of(),
                            false,
                            ClientOwner.Official,
                            restrictedSuperGroupIds = setOf(UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")),
                        ),
                    )
                val client = database.commitTransaction { creation.insertIn(this, prepared).client }
                val access = access(database)
                assertFalse(access.allowed(userId, client.uid))
                beforeSecondRead = {
                    database.executeSqlScript(
                        """
                        UPDATE g_user SET locked = TRUE WHERE user_id = '${userId.value}';
                        INSERT INTO g_membership (created_at, user_id, group_id, post_id, unofficial_post_name)
                        VALUES (NOW(), '${userId.value}', 'ee4153d5-830d-445f-acb3-ec09c53e7c0c',
                            '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', NULL);
                        """.trimIndent(),
                    )
                }
                assertFalse(access.allowed(userId, client.uid))
                assertFalse(access.allowed(userId, client.uid))
            }
        }
    }

    @Test
    fun `available users may access unrestricted clients or any matching restriction`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val creation = CreateClient(database, bcryptCost = 10)
                val input =
                    NewOAuthClient(
                        RedirectUri("https://example.org/callback"),
                        ClientName("Client access"),
                        LocalizedText.of(),
                        false,
                        ClientOwner.Official,
                    )
                val unrestricted = creation.prepare(input)
                val restricted =
                    creation.prepare(
                        input.copy(
                            restrictedSuperGroupIds =
                                setOf(
                                    UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab"),
                                    UUID.fromString("aed27030-ad90-4526-855c-1e909b1dcecb"),
                                ),
                        ),
                    )
                val clients =
                    database.commitTransaction {
                        listOf(creation.insertIn(this, unrestricted).client, creation.insertIn(this, restricted).client)
                    }
                val access = access(database)
                for (client in clients) {
                    assertTrue(access.allowed(userId, client.uid))
                    assertFalse(access.allowed(UserId.generate(), client.uid))
                }
                database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${userId.value}'")
                for (client in clients) assertFalse(access.allowed(userId, client.uid))
                database.executeSqlScript("UPDATE g_user SET locked = NULL WHERE user_id = '${userId.value}'")
                for (client in clients) assertTrue(access.allowed(userId, client.uid))
                database.executeSqlScript("UPDATE g_client SET client_id = NULL")
                for (client in clients) assertFalse(access.allowed(userId, client.uid))
                database.commitTransaction {
                    assertFailsWith<IllegalStateException> { access.allowed(userId, clients.first().uid) }
                }
            }
        }
    }

    private fun access(database: DatabaseFactory) =
        OAuthClientAccess(
            database,
            OAuthProtocolClients(database),
            UserQueries(database),
            OrganizationQueries(database),
        )

    private companion object {
        val userId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
    }
}
