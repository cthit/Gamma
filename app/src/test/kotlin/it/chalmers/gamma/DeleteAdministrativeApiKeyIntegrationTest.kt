package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.DeleteApiKey
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.AdministratorAccess
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DeleteAdministrativeApiKeyIntegrationTest {
    @Test
    fun `stale administrator authority cannot delete a key`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val keys = ApiKeyQueries(database)
                val id = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
                val before =
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.findApiKeyIn(this, id)
                    }
                val actor = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")), true)
                assertFailsWith<AccessDenied> {
                    DeleteAdministrativeApiKey(
                        database,
                        AdministratorAccess(database),
                        DeleteApiKey(database),
                    ).delete(actor, id)
                }
                assertEquals(
                    before,
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.findApiKeyIn(this, id)
                    },
                )
            }
        }
    }

    @Test
    fun `current authority permits deletion and missing keys remain a distinct outcome`() =
        withDatabase { database ->
            val operation = DeleteAdministrativeApiKey(database, AdministratorAccess(database), DeleteApiKey(database))
            operation.delete(administrator, fixtureId)
            assertNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                },
            )
            assertFailsWith<ApiAccessNotFound> { operation.delete(administrator, fixtureId) }
            assertFailsWith<AccessDenied> { operation.delete(Actor.Anonymous, fixtureId) }
        }

    @Test
    fun `administrative deletion rejects an enclosing transaction without changing the key`() =
        withDatabase { database ->
            val operation = DeleteAdministrativeApiKey(database, AdministratorAccess(database), DeleteApiKey(database))
            val previous =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    operation.delete(
                        administrator,
                        fixtureId,
                    )
                }
            }
            assertEquals(
                previous,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                },
            )
        }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")))
        val fixtureId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
    }
}
