package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateClientIntegrationTest {
    @Test
    fun `client credentials are prepared outside a database transaction`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val random =
                    object : SecureRandom() {
                        override fun nextBytes(bytes: ByteArray) {
                            assertNull(TransactionManager.currentOrNull())
                            super.nextBytes(bytes)
                        }
                    }
                CreateClient(database, bcryptCost = 10, random = random).let { creation ->
                    val prepared = creation.prepare(input)
                    database.commitTransaction { creation.insertIn(this, prepared) }
                }
            }
        }
    }

    @Test
    fun `creation persists scopes and restrictions while keeping raw secrets out of representations`() =
        withDatabase { database ->
            val restriction = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
            val creation = CreateClient(database, bcryptCost = 10)
            val prepared =
                creation.prepare(
                    input.copy(includeEmailScope = true, restrictedSuperGroupIds = setOf(restriction)),
                )
            assertTrue(prepared.secret.value !in prepared.toString())
            val result = database.commitTransaction { creation.insertIn(this, prepared) }
            assertTrue(result.secret.value !in result.toString())
            assertEquals(setOf(Scope.OPENID, Scope.PROFILE, Scope.EMAIL), result.client.scopes)
            assertEquals(setOf(restriction), result.client.restrictedSuperGroupIds)
            assertEquals(result.client, OAuthProtocolClients(database).serverClient(result.client.uid)?.client)
            assertTrue(clientSecretMatches(database, result.client.clientId, result.secret))
        }

    @Test
    fun `preparation rejects an enclosing transaction before randomness`() =
        withDatabase { database ->
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        error("must not prepare inside a transaction")
                    }
                }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    CreateClient(
                        database,
                        bcryptCost = 10,
                        random = random,
                    ).let { creation ->
                        val prepared = creation.prepare(input)
                        database.commitTransaction { creation.insertIn(this, prepared) }
                    }
                }
            }
        }

    @Test
    fun `creation participates in caller rollback and rejects foreign or completed transactions`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val creation = CreateClient(database, bcryptCost = 10)
                    val prepared = creation.prepare(input)
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            creation.insertIn(this, prepared)
                            throw IllegalArgumentException("later participant rejected creation")
                        }
                    }
                    assertNull(OAuthProtocolClients(database).serverClient(prepared.uid)?.client)
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> {
                            creation.insertIn(
                                this,
                                prepared,
                            )
                        }
                    }
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction { completed = this }
                    assertFailsWith<IllegalStateException> { creation.insertIn(completed, prepared) }
                }
            }
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val input =
            NewOAuthClient(
                RedirectUri("https://example.org/callback"),
                ClientName("Creation test"),
                LocalizedText.of(),
                false,
                ClientOwner.Official,
            )
    }
}
