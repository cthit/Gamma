package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeleteClientIntegrationTest {
    @Test
    fun `deletion rolls back with its caller and only accepts the transaction that locked the client`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val creation = CreateClient(database, bcryptCost = 10)
                    val prepared =
                        creation.prepare(
                            NewOAuthClient(
                                RedirectUri("https://example.org/callback"),
                                ClientName("Delete participant"),
                                LocalizedText.of(),
                                false,
                                ClientOwner.Official,
                            ),
                        )
                    val created = database.commitTransaction { creation.insertIn(this, prepared) }
                    val deletion = DeleteClient(database)
                    lateinit var target: LockedClientDeletion
                    lateinit var completed: JdbcTransaction
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            completed = this
                            target = deletion.lockIn(this, created.client.uid)
                            assertEquals(ClientOwner.Official, target.owner)
                            deletion.deleteIn(this, target)
                            throw IllegalArgumentException("later participant failed")
                        }
                    }
                    assertNotNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                    assertFailsWith<IllegalStateException> { deletion.lockIn(completed, created.client.uid) }
                    assertFailsWith<IllegalStateException> { deletion.deleteIn(completed, target) }
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> { deletion.lockIn(this, created.client.uid) }
                        assertFailsWith<IllegalStateException> { deletion.deleteIn(this, target) }
                    }
                    database.commitTransaction {
                        assertFailsWith<IllegalStateException> { deletion.deleteIn(this, target) }
                        deletion.deleteIn(this, deletion.lockIn(this, created.client.uid))
                    }
                    assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                }
            }
        }
    }
}
