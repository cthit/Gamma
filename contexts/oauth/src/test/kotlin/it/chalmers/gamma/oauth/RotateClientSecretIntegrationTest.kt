package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RotateClientSecretIntegrationTest {
    @Test
    fun `reservation and replacement participate in their caller rollback`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val created = create(database)
                val rotation = RotateClientSecret(database, bcryptCost = 10)
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction {
                        rotation.reserveIn(this, rotation.lockIn(this, created.client.uid), UUID.randomUUID())
                        throw IllegalArgumentException("later participant rejected reservation")
                    }
                }
                assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
                val reservation =
                    database.commitTransaction {
                        rotation.reserveIn(this, rotation.lockIn(this, created.client.uid), UUID.randomUUID())
                    }
                val prepared = rotation.prepare(reservation)
                assertTrue(prepared.secret.value !in prepared.toString())
                assertFailsWith<IllegalArgumentException> {
                    database.commitTransaction {
                        rotation.replaceIn(this, rotation.lockIn(this, created.client.uid), prepared)
                        throw IllegalArgumentException("later participant rejected replacement")
                    }
                }
                assertEquals(1, database.tableRowCount("g_client_secret_rotation"))
                assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
                database.commitTransaction {
                    rotation.replaceIn(this, rotation.lockIn(this, created.client.uid), prepared)
                }
                assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
                assertFalse(clientSecretMatches(database, created.client.clientId, created.secret))
                assertTrue(clientSecretMatches(database, created.client.clientId, prepared.secret))
            }
        }
    }

    @Test
    fun `phases reject foreign completed and mismatched transactions and clients`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val created = create(database)
                    val unrelated = create(database)
                    val rotation = RotateClientSecret(database, bcryptCost = 10)
                    lateinit var completed: JdbcTransaction
                    lateinit var target: LockedClientSecret
                    val id = UUID.randomUUID()
                    val reservation =
                        database.commitTransaction {
                            completed = this
                            target = rotation.lockIn(this, created.client.uid)
                            rotation.reserveIn(this, target, id)
                        }
                    val prepared = rotation.prepare(reservation)
                    assertFailsWith<IllegalStateException> { rotation.lockIn(completed, created.client.uid) }
                    assertFailsWith<IllegalStateException> { rotation.reserveIn(completed, target, id) }
                    assertFailsWith<IllegalStateException> { rotation.replaceIn(completed, target, prepared) }
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> { rotation.lockIn(this, created.client.uid) }
                        assertFailsWith<IllegalStateException> { rotation.reserveIn(this, target, id) }
                        assertFailsWith<IllegalStateException> { rotation.replaceIn(this, target, prepared) }
                    }
                    database.commitTransaction {
                        assertFailsWith<IllegalStateException> { rotation.reserveIn(this, target, id) }
                        assertFailsWith<IllegalStateException> { rotation.replaceIn(this, target, prepared) }
                        assertFailsWith<IllegalStateException> {
                            rotation.replaceIn(this, rotation.lockIn(this, unrelated.client.uid), prepared)
                        }
                        assertFailsWith<IllegalStateException> { rotation.prepare(reservation) }
                        assertFailsWith<IllegalStateException> { rotation.release(created.client.uid, id) }
                    }
                    rotation.release(created.client.uid, id)
                    assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
                    assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
                }
            }
        }
    }

    private fun create(database: DatabaseFactory): CreatedOAuthClient {
        val creation = CreateClient(database, bcryptCost = 10)
        val prepared =
            creation.prepare(
                NewOAuthClient(
                    RedirectUri("https://example.org/callback"),
                    ClientName("Rotation participant"),
                    LocalizedText.of(),
                    false,
                    ClientOwner.Official,
                ),
            )
        return database.commitTransaction { creation.insertIn(this, prepared) }
    }
}
