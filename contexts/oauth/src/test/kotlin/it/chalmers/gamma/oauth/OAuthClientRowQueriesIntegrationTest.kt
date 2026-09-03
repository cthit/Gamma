package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OAuthClientRowQueriesIntegrationTest {
    @Test
    fun `legacy client rows preserve hydration filtering and server lookup contracts`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}")).use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password),
            ).use { database ->
                val clients = OAuthClientStore(database, bcryptCost = 10)
                val ownerId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")

                run {
                    val alpha = clients.createClient(newClient("Alpha client", ClientOwner.User(ownerId)))
                    val middle = clients.createClient(newClient("Middle client", ClientOwner.User(ownerId)))
                    val zulu = clients.createClient(newClient("Zulu client", ClientOwner.Official))
                    prepareLegacyRows(postgres, alpha.client.uid, middle.client.uid)

                    val expectedAlpha =
                        alpha.client.copy(
                            description = LocalizedText.of("", ""),
                            scopes = setOf(Scope.OPENID, Scope.PROFILE),
                        )
                    val expectedZulu = zulu.client.copy(scopes = setOf(Scope.OPENID, Scope.PROFILE))

                    assertEquals(listOf(expectedAlpha, expectedZulu), clients.listClients(null))
                    assertEquals(listOf(expectedAlpha), clients.listClients(ownerId))
                    assertEquals(expectedAlpha, clients.findClient(alpha.client.uid))
                    assertEquals(expectedAlpha, clients.findClient(alpha.client.clientId))
                    assertNull(clients.findClient(middle.client.uid))
                    assertNull(clients.findClient(middle.client.clientId))

                    val expectedServerClient =
                        OAuthServerClient(
                            expectedAlpha,
                            readEncodedSecret(postgres, alpha.client.uid),
                        )
                    assertEquals(expectedServerClient, clients.serverClient(alpha.client.uid))
                    assertEquals(expectedServerClient, clients.serverClient(alpha.client.clientId))
                    assertNull(
                        clients.serverClient(
                            ClientUid(UUID.fromString("59000000-0000-0000-0000-000000000010")),
                        ),
                    )
                    assertNull(clients.serverClient(ClientId("Z".repeat(30))))
                }
            }
        }
    }

    private fun newClient(
        name: String,
        owner: ClientOwner,
    ) = NewOAuthClient(
        redirectUri = RedirectUri("https://client.example.org/callback"),
        name = ClientName(name),
        description = LocalizedText.of("Svensk beskrivning", "English description"),
        includeEmailScope = false,
        owner = owner,
    )

    private fun prepareLegacyRows(
        postgres: PostgresTestEnvironment,
        alphaClientUid: ClientUid,
        skippedClientUid: ClientUid,
    ) {
        postgres.connection { connection ->
            connection
                .prepareStatement("UPDATE g_client SET description = NULL WHERE client_uid = ?")
                .use { statement ->
                    statement.setObject(1, alphaClientUid.value)
                    assertEquals(1, statement.executeUpdate())
                }
            connection
                .prepareStatement(
                    "UPDATE g_client_scope SET scope = ? WHERE client_uid = ? AND scope = ?",
                ).use { statement ->
                    statement.setString(1, "pRoFiLe")
                    statement.setObject(2, alphaClientUid.value)
                    statement.setString(3, "PROFILE")
                    assertEquals(1, statement.executeUpdate())
                }
            connection
                .prepareStatement("UPDATE g_client SET client_id = NULL WHERE client_uid = ?")
                .use { statement ->
                    statement.setObject(1, skippedClientUid.value)
                    assertEquals(1, statement.executeUpdate())
                }
            connection.commit()
        }
    }

    private fun readEncodedSecret(
        postgres: PostgresTestEnvironment,
        clientUid: ClientUid,
    ): String =
        postgres.connection { connection ->
            connection
                .prepareStatement("SELECT client_secret FROM g_client WHERE client_uid = ?")
                .use { statement ->
                    statement.setObject(1, clientUid.value)
                    statement.executeQuery().use { result ->
                        check(result.next())
                        result.getString(1)
                    }
                }
        }
}
