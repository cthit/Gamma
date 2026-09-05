package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OAuthProtocolClientsIntegrationTest {
    @Test
    fun `protocol lookup keeps scopes metadata restrictions and secret in one snapshot`() {
        for (byUid in listOf(false, true)) {
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
                                val change = beforeThirdRead
                                beforeThirdRead = null
                                change?.invoke()
                            }
                        }
                    }
                DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                    val creation = CreateClient(database, bcryptCost = 10)
                    val prepared = creation.prepare(input)
                    val created = database.commitTransaction { creation.insertIn(this, prepared) }
                    val lookup = OAuthProtocolClients(database)
                    val before = assertNotNull(lookup.serverClient(created.client.uid))
                    val replacement = creation.prepare(input.copy(name = ClientName("After snapshot")))
                    reads = 0
                    beforeThirdRead = {
                        database.executeSqlScript(
                            """
                            UPDATE g_client SET pretty_name = 'After snapshot', client_secret = '${replacement.storedSecret}'
                                WHERE client_uid = '${created.client.uid.value}';
                            DELETE FROM g_client_scope WHERE client_uid = '${created.client.uid.value}';
                            INSERT INTO g_client_scope (client_uid, scope, created_at)
                                VALUES ('${created.client.uid.value}', 'EMAIL', NOW());
                            DELETE FROM g_client_restriction_super_group
                                WHERE restriction_id = '${created.client.uid.value}';
                            """.trimIndent(),
                        )
                    }
                    val result =
                        if (byUid) {
                            lookup.serverClient(created.client.uid)
                        } else {
                            lookup.serverClient(created.client.clientId)
                        }
                    assertEquals(before, result)
                    val after = assertNotNull(lookup.serverClient(created.client.uid))
                    assertEquals(ClientName("After snapshot"), after.client.name)
                    assertEquals(setOf(Scope.OPENID, Scope.EMAIL), after.client.scopes)
                    assertEquals(emptySet(), after.client.restrictedSuperGroupIds)
                    assertEquals(replacement.storedSecret, after.encodedSecret)
                }
            }
        }
    }

    @Test
    fun `protocol lookups reject an enclosing transaction`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val lookup = OAuthProtocolClients(database)
                database.commitTransaction {
                    assertFailsWith<IllegalStateException> { lookup.serverClient(ClientUid.generate()) }
                    assertFailsWith<IllegalStateException> { lookup.serverClient(ClientId("Z".repeat(30))) }
                }
            }
        }
    }

    @Test
    fun `protocol lookup preserves missing hidden and restricted client behavior`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val creation = CreateClient(database, bcryptCost = 10)
                val prepared = creation.prepare(input)
                val created = database.commitTransaction { creation.insertIn(this, prepared) }
                val lookup = OAuthProtocolClients(database)
                val found = assertNotNull(lookup.serverClient(created.client.uid))
                assertEquals(created.client, found.client)
                assertEquals(found, lookup.serverClient(created.client.clientId))
                assertTrue(found.encodedSecret !in found.toString())
                assertEquals(
                    setOf(restriction),
                    database.commitTransaction(readOnly = true) {
                        lookup.restrictionsIn(this, created.client.uid)
                    },
                )
                assertNull(lookup.serverClient(ClientUid.generate()))
                assertNull(lookup.serverClient(ClientId("Z".repeat(30))))
                assertNull(
                    database.commitTransaction(readOnly = true) { lookup.restrictionsIn(this, ClientUid.generate()) },
                )
                database.executeSqlScript(
                    "UPDATE g_client SET client_id = NULL WHERE client_uid = '${created.client.uid.value}'",
                )
                assertNull(lookup.serverClient(created.client.uid))
                assertNull(lookup.serverClient(created.client.clientId))
                assertNull(
                    database.commitTransaction(readOnly = true) { lookup.restrictionsIn(this, created.client.uid) },
                )
            }
        }
    }

    private companion object {
        val restriction: UUID = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
        val input =
            NewOAuthClient(
                RedirectUri("https://example.org/callback"),
                ClientName("Before snapshot"),
                LocalizedText.of(),
                false,
                ClientOwner.Official,
                restrictedSuperGroupIds = setOf(restriction),
            )
    }
}
