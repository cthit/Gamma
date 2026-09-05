package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ClientAuthoritiesIntegrationTest {
    @Test
    fun `authority changes participate in caller rollback and require the transaction that locked the client`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val creation = CreateClient(database, bcryptCost = 10)
                    val prepared =
                        creation.prepare(
                            NewOAuthClient(
                                RedirectUri("https://example.org/callback"),
                                ClientName("Authority participation"),
                                LocalizedText.of(),
                                false,
                                ClientOwner.Official,
                            ),
                        )
                    val uid = database.commitTransaction { creation.insertIn(this, prepared).client.uid }
                    val authorities = ClientAuthorities(database)
                    val name = AuthorityName("manage")
                    val userId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
                    val store = OAuthClientQueries(database)
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            authorities.createIn(this, authorities.lockIn(this, uid), name, setOf(userId), emptySet())
                            throw IllegalArgumentException("later participant rejected creation")
                        }
                    }
                    assertEquals(
                        emptyList(),
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            store.authoritiesIn(this, uid)
                        },
                    )
                    lateinit var target: LockedClientAuthorities
                    lateinit var completed: JdbcTransaction
                    database.commitTransaction {
                        completed = this
                        target = authorities.lockIn(this, uid)
                        authorities.createIn(this, target, name, setOf(userId), emptySet())
                    }
                    val before =
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            store.authoritiesIn(this, uid)
                        }
                    assertFailsWith<IllegalArgumentException> {
                        database.commitTransaction {
                            authorities.deleteIn(this, authorities.lockIn(this, uid), name)
                            throw IllegalArgumentException("later participant rejected deletion")
                        }
                    }
                    assertEquals(
                        before,
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            store.authoritiesIn(this, uid)
                        },
                    )
                    assertFailsWith<IllegalStateException> { authorities.lockIn(completed, uid) }
                    assertFailsWith<IllegalStateException> { authorities.deleteIn(completed, target, name) }
                    database.commitTransaction {
                        assertFailsWith<IllegalStateException> { authorities.deleteIn(this, target, name) }
                        assertFailsWith<IllegalStateException> {
                            authorities.createIn(this, target, AuthorityName("view"), emptySet(), emptySet())
                        }
                    }
                    other.commitTransaction {
                        assertFailsWith<IllegalStateException> { authorities.lockIn(this, uid) }
                        assertFailsWith<IllegalStateException> { authorities.deleteIn(this, target, name) }
                    }
                    assertEquals(
                        before,
                        database.commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            store.authoritiesIn(this, uid)
                        },
                    )
                }
            }
        }
    }
}
