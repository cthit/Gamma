package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiTokenVerificationCache
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.apiaccess.RotateApiKey
import it.chalmers.gamma.apiaccess.StoredApiCredential
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.AdministratorAccess
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RotateAdministrativeApiKeyIntegrationTest {
    @Test
    fun `stale administrator authority cannot rotate a key`() {
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
                    RotateAdministrativeApiKey(
                        database,
                        AdministratorAccess(database),
                        RotateApiKey(database),
                    ).rotate(actor, id)
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
    fun `current administrator authority permits rotation despite a false cached flag`() =
        withDatabase { database ->
            val operation =
                RotateAdministrativeApiKey(
                    database,
                    AdministratorAccess(database),
                    RotateApiKey(database, bcryptCost = 10),
                )
            val result = operation.rotate(administrator, fixtureId)
            assertEquals(1, result.apiKey.version)
            assertNotNull(ApiCredentialAuthenticator(database).authenticate(fixtureId, result.token))
        }

    @Test
    fun `demotion during credential preparation rejects the final mutation and cache publication`() =
        withDatabase { database ->
            val previous =
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                }
            var publications = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        assertNull(TransactionManager.currentOrNull())
                        database.executeSqlScript(
                            "DELETE FROM g_admin_user WHERE user_id = '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f'",
                        )
                        super.nextBytes(bytes)
                    }
                }
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        publications++
                    }
                }
            val operation =
                RotateAdministrativeApiKey(
                    database,
                    AdministratorAccess(database),
                    RotateApiKey(database, cache, bcryptCost = 10, random = random),
                )
            assertFailsWith<AccessDenied> { operation.rotate(administrator, fixtureId) }
            assertEquals(
                previous,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, fixtureId)
                },
            )
            assertEquals(0, publications)
            assertNotNull(
                ApiCredentialAuthenticator(
                    database,
                ).authenticate(fixtureId, RawApiToken("gamma-info-regression-token-000001")),
            )
        }

    @Test
    fun `denied and nested requests reject before credential work`() =
        withDatabase { database ->
            var preparations = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        preparations++
                        super.nextBytes(bytes)
                    }
                }
            val operation =
                RotateAdministrativeApiKey(
                    database,
                    AdministratorAccess(database),
                    RotateApiKey(database, bcryptCost = 10, random = random),
                )
            assertFailsWith<AccessDenied> { operation.rotate(Actor.Anonymous, ApiKeyId(UUID.randomUUID())) }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    operation.rotate(
                        administrator,
                        fixtureId,
                    )
                }
            }
            assertEquals(0, preparations)
        }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")))
        val fixtureId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
    }
}
